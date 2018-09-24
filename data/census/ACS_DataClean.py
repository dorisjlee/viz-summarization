import glob
from state_dict import states
import pandas as pd
import numpy as np

df_stack=[]
for state_abbrev in states.keys(): 
    df_tbl = None
    datafiles = glob.glob("{}/*.csv".format(state_abbrev))
    if len(datafiles)!=0:
        for data in datafiles: 
            df = pd.read_csv(data)
            #df=df[df.keys()[6:]]
            df = df[['NAME']+list(df.filter(regex='EST').keys())]
            df["state_abbrev"] = state_abbrev
            df["state_name"] = states[state_abbrev]
            if not np.isnan(df.ix[0][1]):
                if df_tbl is None:
                    df_tbl = df
                else:
                    df_tbl = df_tbl.merge(df)    
            else:
                print "Bad: ",data
        df_stack.append(df_tbl)
df_all = pd.concat(df_stack)

df_all["NAME"]=df_all["NAME"].apply(lambda x : x.split(',')[0])

col_names = df_all.filter(regex="EST").keys()
col = col_names[0]

lookup_colname = pd.read_csv("ACS_5yr_Seq_Table_Number_Lookup.txt")


col_dict={}
for col in col_names:
    table, line , EST= col.split("_")
    if line =='1': 
        selected_table = lookup_colname[(lookup_colname["Table ID"]==table)]
        full_column_name = selected_table[selected_table["Table Title"].str.contains("Universe")]["Table Title"].values[0].split(':')[-1].lstrip()
    else: 
        full_column_name=lookup_colname[(lookup_colname["Table ID"]==table)&(lookup_colname["Line Number"]==line)]["Table Title"].values[0]
    col_dict[col]=full_column_name
    # print full_column_name

df_all = df_all.rename(index=str,columns=col_dict)
df_all = df_all.dropna()
df_all.to_csv('census.csv')
print "Saved to : census.csv"
import sqlalchemy
engine = sqlalchemy.create_engine('sqlite:///census.sqlite',convert_unicode=True,encoding = 'utf')
df_all.to_sql("census",engine)
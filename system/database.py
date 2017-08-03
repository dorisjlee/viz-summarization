from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import create_engine
import pandas as pd
import json


'''
Configure Flask by providing the PostgreSQL URI so that the app is able to connect to the database, through
'''



#data = pd.read_csv('../data/titanic/titanic.csv',index_col=0)
engine = create_engine("postgresql://summarization:lattice@localhost:5433")
# data.to_sql(name='titanic', con=engine, if_exists = 'replace', index=False)

connection = engine.connect()


def initialize_DB():
	'''
	initialize database, if first time running this, then create engine should check if DB called summarization exist or not
	'''
	raise NotImplementedError

def upload_data():
	'''
	User uploads data from frontend using a csv file
	This function uploads the data into Postgres DB
	
	1) For frontend, look at fileUploader.js and index.html in ZV 
	2) Frontend send request to backend after submit the form 
	3) Then in this function we take the request, read in as a pandas table
	4) then upload onto the sql table 
	'''
	raise NotImplementedError
def get_tables():
    '''
        Get a list of all the tables inside the viz-summarization folder
        summarization=# SELECT TABLE_NAME  FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE' AND  TABLE_SCHEMA='public' ORDER BY TABLE_NAME;
        table_name
        ------------
        titanic
        (1 row)
        We want to be able to retreive this in the front end so that the uplaod dataset dropdown menu updates dynamically.
    '''
    result = connection.execute("SELECT TABLE_NAME  FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE' AND  TABLE_SCHEMA='public' ORDER BY TABLE_NAME;")
    print "table"
    print '--------'
    ret = []
    for row in result:
        ret.append(str(row["table_name"]))
    return json.dumps(ret)

ret = get_tables()
print ret


def get_columns(tablename):
  '''
  Get a list of all the columns inside a particular table to display to the front end 
  in the x and y axis selection panel dropdown menu

  SELECT column_name FROM information_schema.columns WHERE table_schema = 'public' AND table_name = '<tablename>';

  e.g. 
  summarization=# SELECT column_name FROM information_schema.columns WHERE table_schema = 'public' AND table_name = 'titanic';
   column_name
  -------------
   Name
   PClass
   Age
   Sex
   Survived
   SexCode
  (6 rows)
  '''
  result = connection.execute("SELECT column_name FROM information_schema.columns WHERE table_schema = 'public' AND table_name = " + "'" + tablename + "';")
  print "column_name"
  print '--------'
  ret = []
  for row in result:
      ret.append(str(row["column_name"]))
  return json.dumps(ret)


r2 = get_columns("titanic")
print r2
# def query_vizData(tablename,x_attr,y_attr, agg_func, filters):
# 	'''
# 	Constructs a typical query for each visualization 
# 	1) SELECT <agg_func>(<y_attr>) FROM  <tablename> WHERE <filters> GROUPBY <x_attr> 
# 	e.g. SELECT SUM(Population) FROM census WHERE RACE=Asian & GENDER=Female GROUPBY GENDER
# 	2) Read retreived results and store it as a tuple
# 	3) return tuples for each bar in the visualization [a1,a2,a3]
#   #str = "SELECT" + agg_func + "(" + y_attr + ")" + "FROM" + tablename + "WHERE" + filters + "GROUPBY" + x_attr
#   #result = connection.execute(str)
#   '''
#   raise NotImplementedError
import pandas as pd
from sqlalchemy import create_engine
data = pd.read_csv('../data/turn_cheetah_query_log_for_uiuc_v1.csv')
data = data.dropna(axis=0,how='any') # Drop any columns with NaN values (since it messes with the combination code)
engine = create_engine("postgresql://summarization:lattice@localhost:5432")
data.to_sql(name='turn', con=engine, if_exists = 'replace', index=False)


import pandas as pd
from sqlalchemy import create_engine
data = pd.read_csv('../data/turn_cheetah_query_log_for_uiuc_v1.csv')
engine = create_engine("postgresql://summarization:lattice@localhost:5432")
data.to_sql(name='turn', con=engine, if_exists = 'replace', index=False)


import pandas as pd
from sqlalchemy import create_engine
data = pd.read_csv('../data/titanic.csv')
engine = create_engine("postgresql://summarization:lattice@localhost:5432")
data.to_sql(name='titanic', con=engine, if_exists = 'replace', index=False)


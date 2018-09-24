import pandas as pd
from sqlalchemy import create_engine
data = pd.read_csv('mushrooms.csv')
data = data.dropna(axis=0,how='any') # Drop any columns with NaN values (since it messes with the combination code)
engine = create_engine("postgresql://summarization:lattice@localhost:5432")
data.to_sql(name='mushroom', con=engine, if_exists = 'replace', index=False)


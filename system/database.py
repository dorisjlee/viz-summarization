from flask.ext.sqlalchemy import SQLAlchemy
app.config['SQLALCHEMY_DATABASE_URI'] = 'postgresql://localhost/viz-summarization'
db = SQLAlchemy(app)

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
def get_all_tables():
	'''
	Get a list of all the tables inside the viz-summarization folder
	summarization=# SELECT TABLE_NAME  FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE' AND  TABLE_SCHEMA='public' ORDER BY TABLE_NAME;
	table_name
	------------
	 titanic
	(1 row)
	We want to be able to retreive this in the front end so that the uplaod dataset dropdown menu updates dynamically.
	'''
	raise NotImplementedError
def get_all_columns(tablename):
	'''
	Get a list of all the columns inside a particular table to display to the front end 
	in the x and y axis selection panel dropdown menu

	
	'''
	raise NotImplementedError
def construct_query(tablename,x_attr,y_attr, agg_func, filters):
	'''
	Constructs a typical query for each visualization 
	1) SELECT <agg_func>(<y_attr>) FROM  <tablename> WHERE <filters> GROUPBY <x_attr> 
	e.g. SELECT SUM(Population) FROM census WHERE RACE=Asian & GENDER=Female GROUPBY GENDER
	2) Read retreived results and store it as a tuple
	3) return tuples for each bar in the visualization [a1,a2,a3]
	'''
	raise NotImplementedError

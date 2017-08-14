from flask import Flask, redirect, url_for, request, session
from flask import render_template
from Data import *
from database import *
from vizGeneration import *

from flask_sqlalchemy import SQLAlchemy
import json


app = Flask(__name__, static_url_path = '', static_folder = 'static', template_folder = 'templates')
app.secret_key = "super secret key"



app.config['SEND_FILE_MAX_AGE_DEFAULT'] = 0
app.config['SQLALCHEMY_DATABASE_URI'] = 'postgresql://summarization:lattice@localhost:5433'
db = SQLAlchemy(app)

@app.route("/getTreeJSON")
def getTreeJSON():
  (treeTreant, nodeDic) = getJsonFromLattice()
  session['treeTreant'] = treeTreant
  return treeTreant


@app.route("/getColumns")
def getColumns(tablename):
  column_name = get_columns(tablename)
  session['column_name'] = column_name  # a list containing all the column names
  return column_name

'''
@app.route("/handle_data", methods=['GET', 'POST'])
def handle_data():
    table_name = request.form.get('table_select')
    print "handle data"
    print table_name
    return (str(table_name))
'''

@app.route("/getTables")
def getTables():
  table_name = get_tables()
  session['table_name'] = table_name
  print 'lala'
  print table_name
  return table_name

@app.route("/getTree")
def getTree():
  (treeTreant, nodeDic) = getJsonFromLattice()
  session['nodeDic'] = nodeDic
  return nodeDic





@app.route("/", methods=['GET', 'POST'])
def index():
    '''
    (ret, nodeDic) = getJsonFromLattice()
    table_name = get_tables()  # a list containing all the table names
    column_name = get_columns("titanic") # a list containing all the column names

    print "ret: "
    '''
    table_name = session.get('table_name', None)
   
    treeTreant = session.get('treeTreant', None)
    nodeDic = session.get('nodeDic', None)
    column_name = [""]

    select_table_name = str(request.form.get('table_select'))
    session['select_table_name'] = select_table_name
    select_avg_name = str(request.form.get('avg'))
    session['select_avg_name'] = select_avg_name

    filter_list = request.form.getlist('fields[]')
    for key in filter_list:
      print key

    session['filter_list'] = filter_list
    select_aaxis = str(request.form.get('xaxis_select'))
    session['select_aaxis'] = select_aaxis
    select_yaxis = str(request.form.get('yaxis_select'))
    session['select_yaxis'] = select_yaxis

    if session.get('select_table_name', None) is not None:
      column_name = getColumns(session.get('select_table_name', None))

    print "---------------"
    print session.get('select_avg_name', None)
    print session.get('select_yaxis', None)
    print session.get('select_aaxis', None)
    print session.get('select_table_name', None)
    print session.get('filter_list', None)
    print "----------------"

    #if(session.get('select_table_name', None) is not None) and (len(session.get('filter_list', None)) != 0) and (session.get('select_yaxis', None) is not None) and (session.get('select_aaxis', None) is not None) and (session.get('select_avg_name', None) is not None):
      #query_vizData(session.get('select_table_name', None), session.get('select_aaxis', None), session.get('select_yaxis', None), session.get('select_avg_name', None), session.get('filter_list', None))

    generateVizObj("titanic", "survived", "id", "COUNT", ["sex='male'", "age<20"])


    print("line 61")
    print table_name
    print column_name
    return render_template("main.html", treeTreant2 = treeTreant, table = json.loads(table_name), column = json.loads(column_name),
nodeDic = nodeDic)


  


if __name__ == "__main__":
    
    app.debug = True
    app.run()
from config import *
from flask import render_template
from data import *
from database import *
from vizGeneration import *
from flask import jsonify
from flask_sqlalchemy import SQLAlchemy
from query import Query 
import json

db = SQLAlchemy(app)

@app.route("/getTreeJSON")
def getTreeJSON():
  (treeTreant, nodeDic) = getJsonFromLattice()
  session['treeTreant'] = treeTreant
  session['nodeDic'] = nodeDic
  return treeTreant, nodeDic

# @app.route("/getTree")
# def getTree():
#   (treeTreant, nodeDic) = getJsonFromLattice()
#   session['nodeDic'] = nodeDic
#   return nodeDic

@app.route("/getColumns", methods=['POST','GET'])
def getColumns():
  column_name = json.dumps(get_columns(request.form['tablename'])["column_name"])
  session['column_name'] = column_name  # a list containing all the column names
  return jsonify(column_name)

@app.route("/upload_data", methods=['POST'])
def upload_data():
  import pandas as pd
  from sqlalchemy import create_engine
  data = pd.read_csv("mushrooms.csv")
  engine = create_engine("postgresql://summarization:lattice@localhost:5432")
  data.to_sql(name='mushroom', con=engine, if_exists = 'replace', index=False)

@app.route("/getTables")
def getTables():
  tableList = get_tables()
  session['tableList'] = tableList
  return tableList


@app.route("/postQuery", methods=['GET', 'POST'])
def postQuery():
    
    dataset = request.form['dataset']
    xAxis = request.form['xAxis']
    yAxis = request.form['yAxis']
    aggFunc = request.form['aggFunc']
    filters = str(request.form['filters'])
    method = request.form['method']
    query = Query(dataset,xAxis,yAxis,aggFunc,filters,method)
    # run create lattice code 
    return jsonify({"results":"test"})


@app.route("/", methods=['GET', 'POST'])
def index():
    '''
    (ret, nodeDic) = getJsonFromLattice()
    table_name = get_tables()  # a list containing all the table names
    column_name = get_columns("titanic") # a list containing all the column names

    print "ret: "
    '''
    
    all_tables = getTables()
    # dummy example 
    treeTreant, nodeDic = getTreeJSON()
    
    # column_name = [""]
    # select_table_name = str(request.form.get('table_select'))
    # session['select_table_name'] = select_table_name
    # if session.get('select_table_name', None) is not None:
    #   column_name = getColumns(session.get('select_table_name', None))
    # #if(session.get('select_table_name', None) is not None) and (len(session.get('filter_list', None)) != 0) and (session.get('select_yaxis', None) is not None) and (session.get('select_aaxis', None) is not None) and (session.get('select_avg_name', None) is not None):
    #   #query_vizData(session.get('select_table_name', None), session.get('select_aaxis', None), session.get('select_yaxis', None), session.get('select_avg_name', None), session.get('filter_list', None))

    # generateVizObj("titanic", "survived", "id", "COUNT", ["sex='male'", "age<20"])

    # return render_template("main.html", treeTreant2 = treeTreant, all_tables = all_tables,\
    #                         column = json.loads(column_name), nodeDic = nodeDic)
    #return render_template("main.html", treeTreant2 = treeTreant, all_tables = all_tables,\
    #                        nodeDic = nodeDic)
    return render_template("main.html", all_tables = all_tables, treeTreant2 = treeTreant,nodeDic = nodeDic)

if __name__ == "__main__":

    app.debug = True
    app.run()

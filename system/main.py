from config import *
from flask import render_template
from Data import *
from database import *
from vizGeneration import *
from flask import jsonify
from flask_sqlalchemy import SQLAlchemy
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
  print "getColumns"
  print request.form.get('table_select')
  column_name = get_columns('titanic')
  print "column_name:",column_name
  session['column_name'] = column_name  # a list containing all the column names
  return jsonify(column_name)
# @app.route('/more/', methods=['POST'])
# def _more():
#     new_fetched_data = fetch_data() # Data fetch function through sqlalchemy
#     return jsonify('fetched_data', render_template('dynamic_data.html', new_fetched_data=new_fetched_data))
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
  return table_name


@app.route("/getSelectedAxis", methods=['GET', 'POST'])
def getSelectedAxis():
    
    select_avg_name = str(request.form.get('avg'))
    session['select_avg_name'] = select_avg_name

    filter_list = request.form.getlist('fields[]')
    for key in filter_list:
      print key

    session['filter_list'] = filter_list
    select_xaxis = str(request.form.get('xaxis_select'))
    session['select_xaxis'] = select_xaxis
    select_yaxis = str(request.form.get('yaxis_select'))
    session['select_yaxis'] = select_yaxis


    print "---------------"
    print session.get('select_avg_name', None)
    print session.get('select_yaxis', None)
    print session.get('select_xaxis', None)
    print session.get('select_table_name', None)
    print session.get('filter_list', None)
    print "----------------"


@app.route("/", methods=['GET', 'POST'])
def index():
    '''
    (ret, nodeDic) = getJsonFromLattice()
    table_name = get_tables()  # a list containing all the table names
    column_name = get_columns("titanic") # a list containing all the column names

    print "ret: "
    '''
    
    all_tables = getTables()
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
    return render_template("main.html", treeTreant2 = treeTreant, all_tables = all_tables,\
                            nodeDic = nodeDic)

if __name__ == "__main__":

    app.debug = True
    app.run()

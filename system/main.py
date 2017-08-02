from flask import Flask
from flask import render_template
from Data import getJsonFromLattice
from database import *
from flask_sqlalchemy import SQLAlchemy


app = Flask(__name__, static_url_path = '', static_folder = 'static', template_folder = 'templates')

app.config['SEND_FILE_MAX_AGE_DEFAULT'] = 0
app.config['SQLALCHEMY_DATABASE_URI'] = 'postgresql://summarization:lattice@localhost:5433'
db = SQLAlchemy(app)


@app.route("/")
def index():
    ret = getJsonFromLattice()
    table_name = get_tables()  # a list containing all the table names
    column_name = get_columns("titanic") # a list containing all the column names

    print "ret: "
    print ret
    return render_template("main.html", treeTreant2 = ret, table = table_name, column = column_name)

if __name__ == "__main__":
    app.run()

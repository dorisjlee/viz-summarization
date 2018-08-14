from flask import render_template
from app import app

@app.route('/')
def index():
    return render_template("index.html", title="Basic Flask App")
@app.route('/1')
def task1():
    return render_template("task1.html", title="Basic Flask App")
@app.route('/2')
def task2():
    return render_template("task2.html", title="Basic Flask App")

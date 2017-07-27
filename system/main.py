from flask import Flask
from flask import render_template
from Data import getJsonFromLattice
app = Flask(__name__, static_url_path = '', static_folder = 'static', template_folder = 'templates')

app.config['SEND_FILE_MAX_AGE_DEFAULT'] = 0

@app.route("/")
def index():
    ret = getJsonFromLattice()
    print "ret: "
    print ret
    return render_template("main.html", treeTreant2 = ret)

if __name__ == "__main__":
    app.run()

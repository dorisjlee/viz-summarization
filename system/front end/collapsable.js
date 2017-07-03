
   // Array approach
    var config = {
        container: "#collapsable-example",

        animateOnInit: true,
        
        node: {
            collapsable: true

        },
        animation: {
            nodeAnimation: "easeOutBounce",
            nodeSpeed: 700,
            connectorsAnimation: "bounce",
            connectorsSpeed: 700
        }
    },
    malory = {
        //image: "img/test2.jpg"
        //innerHTML: "<div <h3>foo bar</h3> </div>"
        //innerHTML:"<div class=\"main\">hello</div>"
        innerHTML: "#chartid"
    },

    lana = {
        parent: malory,
        innerHTML: "<div <h3>foo bar</h3> </div>"
        //innerHTML:"<div class=\"main\"><hello/div>"
        //innerHTML: "<div class='main'><p><span id='city'></span> - <span id='inches'></span></p></div>"
    },

    figgs = {
        parent: lana,
        //image: "img/figgs.png"
        innerHTML: "#chartid"
    },

    sterling = {
        parent: malory,
        childrenDropLevel: 1,
        //image: "img/sterling.png"
        innerHTML: "<div <h3>foo bar</h3> </div>"
    },

    woodhouse = {
        parent: sterling,
        //image: "img/woodhouse.png"
        innerHTML: "<div <h3>foo bar</h3> </div>"
    },

    pseudo = {
        parent: malory,
        pseudo: true
    },

    cheryl = {
        parent: pseudo,
        //image: "img/cheryl.png"
        innerHTML: "<div <h3>foo bar</h3> </div>"
    },

    pam = {
        parent: pseudo,
        //image: "img/pam.png"
        innerHTML: "<div <h3>foo bar</h3> </div>"
    },

    chart_config = [config, malory, lana, figgs, sterling, woodhouse, pseudo, pam, cheryl];
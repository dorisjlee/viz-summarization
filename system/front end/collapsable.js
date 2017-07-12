
   // Array approach
    var config = {
        container: "#tree_container",

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
        innerHTML: "#chart0"
    },

    lana = {
        parent: malory,
        innerHTML: "#chart1"
    },

    figgs = {
        parent: lana,
        innerHTML: "#chart2"
    },

    sterling = {
        parent: malory,
        childrenDropLevel: 1,
        innerHTML: "#chart3"
    },

    woodhouse = {
        parent: sterling,
        innerHTML: "#chart4"
    },

    pseudo = {
        parent: malory,
        pseudo: true,
        innerHTML: "#chart5"

    },

    cheryl = {
        parent: pseudo,
        innerHTML: "#chart6"
    },

    pam = {
        parent: pseudo,
        innerHTML: "#chart7"
    },

    obama = {
        parent: pseudo,
        innerHTML: "#chart8"
    }

    chart_config = [config, malory, lana, figgs, sterling, woodhouse, pseudo, pam, cheryl, obama];


    function test_chart_collapsable(arrayDiv){
        // On document load, call the render() function to load the graph
        chart_container = document.getElementById("chart_container");

        /*
        c = document.createElement('div');
        c.id = 'chart' + 0;
        chart_container.appendChild(c);
        render_chart(0);
        chart0 = document.getElementById("chart0");
        */
        
        for(var i = 0; i < data.length; i++) {
            arrayDiv[i] = document.createElement('div');
            arrayDiv[i].id = 'chart' + i;
            chart_container.appendChild(arrayDiv[i]);

            p = document.createElement('p');
            spanx = document.createElement('span');
            spanx.id = "xAxis" + i;
            spany = document.createElement('span');
            spany.id = "yAxis" + i;
            p.appendChild(spanx);
            p.appendChild(spany);
            arrayDiv[i].appendChild(p);
            //chart_container.appendChild("<p><span id="xAxis"></span> - <span id="yAxis"></span></p>")

            $('rect').mouseenter(function(){
                $('#xAxis' + i).html(this.className.animVal);
                $('#yAxis' + i).html($(this).attr('id'));
            });
            render_chart(i);
        }

        
        console.log("done");
    }



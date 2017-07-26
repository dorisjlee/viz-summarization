


import networkx as nx
import json

# 	Lattice is a DAG (graph) of Node objects



class Lattice:

    # contain a graph of Node objects
    def __init__(self):
        self.graph = nx.DiGraph()
        self.type = 'summarized' #'full' or summarized
        self.nodeDic = {}
        #

    # not worry about
    def generateDashboard(self):
        raise NotImplementedError

    # testing
    # populate a lattice of given visualizations to test rendering functions
    def populateDummyLattice(self):
        raise NotImplementedError

    # print formatted string summarization of object when 'print latticeObj' is called
    # return number of nodes in lattice and other attributes (would be nice to display graph structure
    # TODO
    def __repr__(self):
        return '(Nodes: %s, numberOfNodes: %s)' % (self.getNodes(), self.numberOfNodes())


        # wrapper function for adding a node
    def addNode(self, node):
        node.id = self.numberOfNodes() + 1
        self.graph.add_node(node)

    # wrapper function for adding multiple nodes
    def addMultiNodes(self, list):
        for node in list:
            self.addNode(node)

    # wrapper function for adding a node with (k,v) attribute
    # [(race,black), (age,15), (gender,female)]
    def addNodePlus(self, node_name, attribute):
        for (k,v) in attribute:
            self.graph.add_node(node_name,k=v)

    # wrapper function for deleting a node
    def deleteNode(self, node_name):
        self.graph.remove_node(node_name)

    # wrapper function for adding an edge
    def addEdge(self, head, tail):
            self.graph.add_edge(head, tail)

    # wrapper function for adding an Edge with (k,v) attribute
    # [(race,black), (age,15), (gender,female)]
    def addEdgePlus(self, head, tail, attribute):
            for (k, v) in attribute:
                self.graph.add_edge(head, tail, k=v)


    # wrapper function for deleting an Edge
    def deleteEdge(self, head, tail):
        self.graph.remove_edge(head, tail)

    # getter
    def getNodes(self):
        return self.graph.nodes()

    # setter
    def getEdges(self):
        return self.graph.edges()

    # number of nodes
    def numberOfNodes(self):
        return self.graph.number_of_nodes()

    # number of edges
    def numberOfEdges(self):
        return self.graph.number_of_edges()


    # generate a node_dic
    def generateNodeDic(self):
        # create a dictionary contains{0: node0, 1: node2}
        node_dic = {}
        for node in self.getNodes():
            each = []
            viz = node.get_viz()
            current = viz[0]
            for idx, val in enumerate(current.X):
                each.append({"xAxis": val, "yAxis": current.data[idx]})
            each.append({"filter": ' '.join(current.filters), "yName": current.Y, "childrenIndex": node.childrenIndex})

            node_dic[node.id] = each
        return node_dic




    #generate a treeNode.json file
    def generateJson(self, root, node_dic):

        tree = "{"
        tree += '"innerHTML"' + ' : ' + '"#chart' + str(root.id) + '"' + "," 

        tree += '"children"' +' : '+'['

        for i in range(len(root.childrenIndex)):
            thisBracket = "{"
            thisBracket += '"innerHTML"' + ' : ' + '"#chart' + str(root.childrenIndex[i]) + '"'+","
        
            #if i != len(root.childrenIndex) - 1:
                #thisBracket += ","
            


            if node_dic[root.childrenIndex[i]][len(node_dic[root.childrenIndex[i]]) - 1]["childrenIndex"] != []:
                arr = node_dic[root.childrenIndex[i]][len(node_dic[root.childrenIndex[i]]) - 1]["childrenIndex"]
                thisBracket += '"children"' +' : '+'['
                for j in range(len(arr)):
                    this = "{"
                    this += '"innerHTML"' + ' : ' + '"#chart' + str(arr[j]) + '"' 
                    if j != len(arr) - 1:
                        this += "},"
                    else:
                        this += "}"
                    thisBracket += this
                if i != len(root.childrenIndex)-1:
                    thisBracket += ']},'
                else:
                    thisBracket += ']}'
                tree += thisBracket

        tree += ']}'
    
        print("generate json")
        print(tree)
        return tree



    # draw a graph
    def drawGraph(self):
        G = self.graph

        # print(G.numberOfEdges())
        p = nx.drawing.nx_pydot.to_pydot(G)
        p.write_png('example2.png')
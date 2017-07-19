from lattice import Lattice
from node import vizNode
import networkx as nx
from vizObj import vizObj
from collections import OrderedDict
import json


# set up the tree example
G = Lattice()
root = vizNode(filters = ["All"])

v1 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
v1.setData([48, 46, 6])
v1.setFilters(root.get_filter())
root.set_viz([v1])

v2 = vizObj()
v2.setX(["Clinton", "Trump", "Others"])
v2.setY("% of vote")
v2.setData([31,62,7])
v2.setFilters(["Race = White", "Gender = M"])




M = vizNode(parents = [root], filters = ["Gender = M"])
F = vizNode(parents = [root], filters = ["Gender = F"])
White = vizNode(parents = [root], filters = ["Race = White"])
Black = vizNode(parents = [root], filters = ["Race = Black"])

WM = vizNode(parents = [White, M], filters = ["Race = White", "Gender = M"])
WF = vizNode(parents=[White, F], filters=["Race = White", "Gender = F"])
BM = vizNode(parents = [Black, M], filters = ["Race = Black", "Gender = M"])
BF = vizNode(parents=[Black, F], filters=["Race = Black", "Gender = F"])

G.addMultiNodes([root,M,F,White,Black,WM,WF,BM,BF])


root.set_children([M,F,White,Black])
M.set_children([WM, BM])
F.set_children([WF, BF])
White.set_children([WM, WF])
Black.set_children([BM, BF])

WM.set_viz([v2])




v3 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
v3.setData([41, 52, 7])
v3.setFilters(M.get_filter())
M.set_viz([v3])

v4 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
v4.setData([54, 41, 5])
v4.setFilters(F.get_filter())
F.set_viz([v4])

v5 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
v5.setData([37, 57, 6])
v5.setFilters(White.get_filter())
White.set_viz([v5])

v6 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
v6.setData([89, 8, 3])
v6.setFilters(Black.get_filter())
Black.set_viz([v6])


v7 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
v7.setData([43, 52, 5])
v7.setFilters(WF.get_filter())
WF.set_viz([v7])



v8 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
v8.setData([82, 13, 5])
v8.setFilters(BM.get_filter())
BM.set_viz([v8])

v9 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
v9.setData([94, 4, 2])
v9.setFilters(BF.get_filter())
BF.set_viz([v9])



# set up the tree example

for nodes in G.getNodes():
    for child in nodes.get_child():
        G.addEdge(nodes, child)





total = []

for nodes in G.getNodes():
    each = []
    viz = nodes.get_viz()
    current = viz[0]
    for idx, val in enumerate(current.X):
      each.append({"xAxis": val, "yAxis": current.data[idx]})
    each.append({"filter": ' '.join(current.filters), "yName": current.Y, "childrenIndex" : nodes.childrenIndex})
    total.append(each)
#print(total)


with open('data.json', 'w') as outfile:
    json.dump(total, outfile)



#create a dictionary contains{0: node0, 1: node2}
node_dic = {}
for node in G.getNodes():
    each = []
    viz = node.get_viz()
    current = viz[0]
    for idx, val in enumerate(current.X):
        each.append({"xAxis": val, "yAxis": current.data[idx]})
    each.append({"filter": ' '.join(current.filters), "yName": current.Y, "childrenIndex" : node.childrenIndex})


    node_dic[node.id] = each


print(node_dic)

with open('nodeDic.json', 'w') as outfile:
    json.dump(node_dic, outfile)




for node in G.getNodes():
    print(node.id)

G = G.graph


#print(G.numberOfEdges())
p=nx.drawing.nx_pydot.to_pydot(G)
p.write_png('example2.png')


tree = OrderedDict()
tree["innerHTML"] = "#chart" + str(root.id)

tree["children"] = []

for each in root.childrenIndex:
    thisBracket = {}
    thisBracket["innerHTML"] = "#chart" + str(each)

    if node_dic[each][len(node_dic[each])-1]["childrenIndex"] != []:
        i = node_dic[each][len(node_dic[each])-1]["childrenIndex"]
        thisBracket["children"] = []
        for c in i:
            this = {}
            this["innerHTML"] = "#chart" + str(c)
            thisBracket["children"].append(this)
        tree["children"].append(thisBracket)


with open('tree.json', 'w') as outfile:
    json.dump(tree, outfile,indent=4)


from lattice import Lattice
from node import singleNode
import networkx as nx
from vizObj import vizObj



# set up the tree example
G = Lattice()
root = singleNode(filters = ["All"])

v1 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
v1.setData([48, 46, 6])
v1.setFilters(root.get_filter())
root.set_viz([v1])

v2 = vizObj()
v2.setX(["Clinton", "Trump", "Others"])
v2.setY("% of vote")
v2.setData([31,62,7])
v2.setFilters(["Race = White", "Gender = M"])


M = singleNode(parents = [root], filters = ["Gender = M"])
F = singleNode(parents = [root], filters = ["Gender = F"])
White = singleNode(parents = [root], filters = ["Race = White"])
Black = singleNode(parents = [root], filters = ["Race = Black"])
root.set_children([M,F,White,Black])

WM = singleNode(parents = [White, M], filters = ["Race = White", "Gender = M"])
WF = singleNode(parents=[White, F], filters=["Race = White", "Gender = F"])
BM = singleNode(parents = [Black, M], filters = ["Race = Black", "Gender = M"])
BF = singleNode(parents=[Black, F], filters=["Race = Black", "Gender = F"])
M.set_children([WM, BM])
F.set_children([WF, BF])
White.set_children([WM, WF])
Black.set_children([BM, BF])

WM.set_viz([v2])










# set up the tree example



G.addMultiNodes([root,M,F,White,Black,WM,WF,BM,BF])
ret = G.getNodes()
ret = set(ret)
l1 = set([root,M,F,White,Black,WM,WF,BM,BF])
r1 = (ret == l1)
print("Test1: \n")
print(r1)




G.addNode(1)
G.addNode(root)
ret2 = set(G.getNodes())
l2 = set([root,M,F,White,Black,WM,WF,BM,BF,1])
r2 = (ret2 == l2)
print("Test2: \n")
print(r2)

G.deleteNode(1)
ret3 = (G.numberOfNodes() == 9)
print("Test3: \n")
print(ret3)


G.addEdge(root, M)
G.addEdge(White, Black)
ret4 = G.getEdges()
r4 = (G.numberOfEdges() == 2)
print("Test4: \n")
print(r4)

G.deleteEdge(White,Black)
ret5 = G.numberOfEdges() == 1
print("Test5: \n")
print(ret5)



r6 = root.get_parent() == []
r7 = root.get_child().sort() == [M,F,White,Black].sort()
print("Test6: \n")
print(r6 and r7)


F.set_parents([M])
r7 = F.get_parent().sort() == [root, M].sort()
t = F.remove_parent(M)
t1 = F.get_parent().sort() == [root].sort()
print("Test8: \n")
print(r7 and t1)


root.set_filters(["lala"])
r8 = root.get_filter() == ["lala"]
root.set_filters(["All"])
r9 = root.get_filter() == ["All"]
print("Test9: \n")
print(r8 and r9)


print(root.get_viz() == [v1])
print(root.get_filter() == v1.filters)
print(WM.get_viz() == [v2])
print(WM.get_filter() == v2.filters)

nx.draw(G.graph)


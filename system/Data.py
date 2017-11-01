from lattice import Lattice
from node import vizNode
import networkx as nx
from vizObj import vizObj
from collections import OrderedDict
import json

def getJsonFromLattice():
    # set up the tree example
    G = Lattice()
    v1 = vizObj(x=["Clinton", "Trump", "Others"],y="% of vote",filters=["All"],\
                agg_func="SUM",tablename="election")
    v1.setData([48, 46, 6])
    root = vizNode(viz=v1, parents=None)

    v2 = vizObj(x=["Clinton", "Trump", "Others"],y="% of vote",filters=["Race = White"],\
                agg_func="SUM",tablename="election")
    v2.setData([31, 62, 7])
    W = vizNode(viz=v2,parents=[root])

    v3 = vizObj(x=["Clinton", "Trump", "Others"],y="% of vote",filters=["Gender = F"],\
                agg_func="SUM",tablename="election")
    v3.setData([21, 70, 9])
    F = vizNode(viz=v3,parents=[root])

    v4 = vizObj(x=["Clinton", "Trump", "Others"], y="% of vote", filters=["Color = Blue"], \
                agg_func="SUM", tablename="election")
    v4.setData([21, 52, 27])
    B = vizNode(viz=v4, parents=[W])

    v5 = vizObj(x=["Clinton", "Trump", "Others"], y="% of vote", filters=["Job = Student"], \
                agg_func="SUM", tablename="election")
    v5.setData([20, 30, 50])
    J = vizNode(viz=v5, parents=[W, B])
    # set up the tree example
    for nodes in G.getNodes():
        for child in nodes.get_child():
            G.addEdge(nodes, child)
    G.addMultiNodes([root,W,F,B,J])
    root.set_children([W,F])
    W.set_children([B])
    W.set_children([J])
    B.set_children([J])


    nodeDic = G.generateNodeDicJsonFile()
    node = G.generateNode(root, nodeDic)
    edge = G.generateEdge(root, nodeDic)
    #print(ret)
    ret2 = '<svg xmlns="http://www.w3.org/2000/svg" width="480" height="260"><g transform="translate(50,30)"><rect x="0" y="0" width="100%" height="100%" fill="#ffffff"></rect><rect x="14" y="0" width="105" height="200" fill="steelblue" class="Clinton" id="48"></rect><rect x="139" y="8.333333333333343" width="105" height="191.66666666666666" fill="steelblue" class="Trump" id="46"></rect><rect x="264" y="175" width="105" height="25" fill="steelblue" class="Others" id="6"></rect><g class="axis" transform="translate(-10, 0)"><g transform="translate(0,200)" style="opacity: 1;"><line class="tick" x2="-6" y2="0"></line><text x="-9" y="0" dy=".32em" style="text-anchor: end;">0</text></g><g transform="translate(0,158.33333333333334)" style="opacity: 1;"><line class="tick" x2="-6" y2="0"></line><text x="-9" y="0" dy=".32em" style="text-anchor: end;">10</text></g><g transform="translate(0,116.66666666666667)" style="opacity: 1;"><line class="tick" x2="-6" y2="0"></line><text x="-9" y="0" dy=".32em" style="text-anchor: end;">20</text></g><g transform="translate(0,75)" style="opacity: 1;"><line class="tick" x2="-6" y2="0"></line><text x="-9" y="0" dy=".32em" style="text-anchor: end;">30</text></g><g transform="translate(0,33.33333333333334)" style="opacity: 1;"><line class="tick" x2="-6" y2="0"></line><text x="-9" y="0" dy=".32em" style="text-anchor: end;">40</text></g><path class="domain" d="M-6,0H0V200H-6"></path></g><g class="axis" transform="translate(0,210)"><g transform="translate(70.5,0)" style="opacity: 1;"><line class="tick" y2="6" x2="0"></line><text y="9" x="0" dy=".71em" style="text-anchor: middle;">Clinton</text></g><g transform="translate(195.5,0)" style="opacity: 1;"><line class="tick" y2="6" x2="0"></line><text y="9" x="0" dy=".71em" style="text-anchor: middle;">Trump</text></g><g transform="translate(320.5,0)" style="opacity: 1;"><line class="tick" y2="6" x2="0"></line><text y="9" x="0" dy=".71em" style="text-anchor: middle;">Others</text></g><path class="domain" d="M0,6V0H390V6"></path></g><text transform="translate(-30, -20)">% of vote</text><text x="195" y="-8px" text-anchor="middle" style="font-size: 16px; text-decoration: underline;">All</text></g></svg>'
    return (nodeDic, node, edge)
    # M = vizNode(parents=[root], filters=["Gender = M"])
    # F = vizNode(parents=[root], filters=["Gender = F"])
    # White = vizNode(parents=[root], filters=["Race = White"])
    # Black = vizNode(parents=[root], filters=["Race = Black"])

    # WM = vizNode(parents=[White, M], filters=["Race = White", "Gender = M"])
    # WF = vizNode(parents=[White, F], filters=["Race = White", "Gender = F"])
    # BM = vizNode(parents=[Black, M], filters=["Race = Black", "Gender = M"])
    # BF = vizNode(parents=[Black, F], filters=["Race = Black", "Gender = F"])

    # G.addMultiNodes([root, M, F, White, Black, WM, WF, BM, BF])
    # root.set_children([M, F, White, Black])
    # M.set_children([WM, BM])
    # F.set_children([WF, BF])
    # White.set_children([WM, WF])
    # Black.set_children([BM, BF])

    # WM.set_viz([v2])

    # v3 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
    # v3.setData([41, 52, 7])
    # v3.setFilters(M.get_filter())
    # M.set_viz([v3])

    # v4 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
    # v4.setData([54, 41, 5])
    # v4.setFilters(F.get_filter())
    # F.set_viz([v4])

    # v5 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
    # v5.setData([37, 57, 6])
    # v5.setFilters(White.get_filter())
    # White.set_viz([v5])

    # v6 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
    # v6.setData([89, 8, 3])
    # v6.setFilters(Black.get_filter())
    # Black.set_viz([v6])

    # v7 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
    # v7.setData([43, 52, 5])
    # v7.setFilters(WF.get_filter())
    # WF.set_viz([v7])

    # v8 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
    # v8.setData([82, 13, 5])
    # v8.setFilters(BM.get_filter())
    # BM.set_viz([v8])

    # v9 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
    # v9.setData([94, 4, 2])
    # v9.setFilters(BF.get_filter())
    # BF.set_viz([v9])

    
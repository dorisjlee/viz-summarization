from lattice import Lattice
from vizObj import vizObj
import networkx as nx


class singleNode:

    '''
    A Node object contains a list of vizObj (data objects)

    '''
    def __init__(self,children=[],parents=[], filters = [], viz = []):
        self.vizObj = viz
        self.children = children
        self.parents = parents
        self.filters = filters

    def __repr__(self):
        return '(Filters: %s, vizObjs: %s)' % (self.filters, self.vizObj)



    def get_parent(self):
        return self.parents

    def get_child(self):
        return self.children

    def get_viz(self):
        return self.vizObj

    def get_filter(self):
        return self.filters

    def set_parents(self, parent):
        self.parents = parent

    def set_children(self, children):
        self.children = children

    def set_viz(self, viz):
        self.vizObj = viz

    def set_filters(self, filters):
        self.filters = filters

    def remove_child(self, child):
        for each in self.children:
            if each == child:
                self.children.remove(each)
                break

    def remove_parent(self, parent):
        for each in self.parents:
            if each == parent:
                self.parents.remove(each)
                break





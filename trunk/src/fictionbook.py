'''
FictionBook2 module
'''

import sys, string
import cbparser

class TexFBook(cbparser.XMLCbParser):

    def __init__(self, outfile):
        cbparser.XMLCbParser.__init__(self)
        self.outfile = outfile
        self.author = None
        self.title = None

    def _uwrite(self, ustr):
        self.f.write(ustr.encode('utf-8')) 
                
    # callbacks

    def start_FictionBook_description_title__info_author(self, attrs):
        self.author={}

    def chars_FictionBook_description_title__info_author_first__name(self, text):
        self.author['first']=text
        
    def chars_FictionBook_description_title__info_author_middle__name(self, text):
        self.author['middle']=text
        
    def chars_FictionBook_description_title__info_author_last__name(self, text):
        self.author['last']=text

    def end_FictionBook_description_title__info_author(self):
        self.author_name = u''
        for k in ['first','middle','last']:
            if self.author.has_key(k):
                v = self.author[k]
            else:
                v = None
            if v:
                if len(self.author_name)==0:
                    self.author_name = v
                else:
                    self.author_name = self.author_name + u' ' + v
        self.f.write("\\author{")
        self._uwrite(self.author_name)
        self.f.write("}\n");

    def chars_FictionBook_description_title__info_book__title(self, text):
        self.f.write("\\title{")
        self._uwrite(text)
        self.f.write("}\n")

    def start_FictionBook(self, attrs):
        self.f = open(self.outfile,"w")
        self.f.write("\\documentclass[11pt]{book}\n")
        self.f.write("\\usepackage{graphicx}\n")
        self.f.write("\\usepackage{url}\n")
        self.f.write("\\usepackage{verbatim}\n")
        self.f.write("\\usepackage[koi8-r]{inputenc}\n")
        self.f.write("\\usepackage[russian]{babel}\n")
        self.f.write("\\usepackage[papersize={9cm,12cm}, margin=4mm, ignoreall, pdftex]{geometry}\n")
        self.f.write("\\begin{document}\n")

    def end_FictionBook(self):
        self.f.write("\\end{document}\n")
        self.f.close()


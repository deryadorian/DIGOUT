1) Goto eclipse File -> Import -> Install -> Install Software Items from File and choose ${git}\ctco-ecom\ide-settigs\eclipse\plugins-set.p2f.
(obsolete) 1) install  findBugs, checkstyle, PMD plugins for eclipse.

(obsolete) 2) Import checkstyle rules. 
	a) goto Preferences/Checkstyle
	b) New ...
	c) specify 
		-type:"External Config File"; 
		-rules name(used locally), 
		-Location should point to trunk/eclipse/checkstyle_rules.xml
		-Additional properties : resource.dir={full path to trunk/eclipse folder}
	d) right click on project , activate checkstyle.
3) use built-in eclipse formatter.
####3) import formatter (trunk/eclipse/formatter.xml) and set as active.
4)
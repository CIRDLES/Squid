<img src="https://github.com/CIRDLES/DRAKE/blob/master/logos/Squid/SquidLogo.png" width="100">

Squid3
=======

[![Build Status](https://travis-ci.org/CIRDLES/Squid.svg?branch=master)](https://travis-ci.org/CIRDLES/Squid)

Please visit [wiki](https://github.com/CIRDLES/Squid/wiki).

[***Click to View New HowTo Videos***](https://www.youtube.com/playlist?list=PLfF8bcNRe2WTWx2IuDaHW_XpLh36bWkUc)

***UPDATE NOV 2021***

We encourage you to download and evaluate the latest Squid3 release, the ".jar" file
found <a href="https://github.com/CIRDLES/Squid/releases" target="_blank">here</a>.  

Squid3 requires
Oracle's <a href="http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html" target="_blank">
Java 1.8</a>. It will not run on later versions of Java EXCEPT as explained below:

With the current release of version 1.9.0, Squid3 is now compatible with the three LTS (long-term support) versions of Java - 8, 11, and 17.  You can read [here](https://en.wikipedia.org/wiki/Java_version_history)
about the history of Java versions.  Squid3 release ".jar"s are built using Java SE8 to run correctly on 8, 11 and 17.  There is an important caveat, however, that the Squid3 graphical user interface is built using JavaFX.  JavaFX is included in most releases of Java 8, but has been separated into a different project in all later Java versions.  The result is that, in general,  a different Squid3 "jar" file must be created for each flavor of operating system - Win, Mac, and Linux, since JavaFX is coupled to the underlying environment. There are various ways to automate this process, but the most elegant and time-saving solution for us is to depend on the <a href="https://bell-sw.com/pages/downloads/#/java-8-lts" target="_blank">Liberica family of Java JDKs and JREs</a>, which provide a "full" version that automatically brokers the JavaFX environment needed for the operating system and only requires one version of the Squid3 "jar" that will run on any system using Liberica's full JDK or JRE 8, 11, or 17.

If you are successfully running Squid3 using Oracle's <a href="http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html" target="_blank">
Java 1.8</a>, the following paragraph is for information only.

The Liberica full JDK or JRE for 8, 11, or 17 for your operating system can be downloaded as a compressed archive and expanded anywhere you choose.  If you want to make this version the default on your operating system, there are many online tutorials to follow. The Java executable is in the "bin" folder and is named "java"  for Mac and Linux, and "java.exe" for Windows.  To run the Squid3 "jar" file, open a terminal window and paste in the path to the java executable, followed by a space character and the flag "-jar" followed by a space character and the path to the Squid3 "jar" file.  

Windows example assuming terminal is currently in folder containing Squid3 "jar":
```text
C:\MYJAVA\jdk8u302-full\bin\java.exe -jar Squid3-1.9.0.jar
```
Mac or Linux example assuming terminal is currently in folder containing Squid3 "jar":
```text
/Users/yourName/Documents/MYJAVA/jdk8u302-full/bin/java -jar Squid3-1.9.0.jar
```
If you need to have a copy of Squid3 that runs on a specific OS and a Java JDK or JRE that does not include JavaFX, we can provide one or provide instructions for you to build one from the source code.

***Note to Developers***

Squid3 can be compiled from the source code by using Gradle 7.2 running Java 8 and it will run on any of the Liberica full JDKs or JREs 8, 11, 17.  If Gradle 7.2 is set to use a higher version of Java (Liberica), then Squid3 will not run on lower numbered versionbs of Java.
```text
gradle clean build 
```
Squid3 "jar" file can then be built:
```text
gradle fatappjar
```

***Note to Users***

Thank you for using Squid3.  We depend on your feedback to keep Squid3 up-to-date and useful to you.  To contribute issues and feedback, please join GitHub <a href="https://github.com/" target="_blank">here</a>. While
using Squid3, the help menu provides a link that initializes a new GitHub issue for you - please use freely.

The current versions - Squid3-**1.9.n.jar** - provide full workflow support for processing and interpreting Shrimp data
outputs in the form of Prawn xml files and 'OP' files including project, data, task, expressions, visualizations,
reports, and parameters management. Squid3 provides for creating and saving Squid3 Project files that have an extension
of ".squid" and are intended to save the current state of work on a project. The workflow is mapped left to right in the
menus, as described below.

The **Project** menu provides for creating new _GEOCHRON_ and _RATIO_ projects. The functionality for _RATIO_ projects
is still under development - please feel free to contribute ideas. A new _GEOCHRON_ project can be created from a single
Prawn XML file, from a single Prawn XML file zipped, from merging two Prawn XML files, or from an 'OP' file. A special
demonstration Squid3 project is available from this menu, thanks to [@NicoleRayner](https://github.com/NicoleRayner).
The project manager allows for naming the project, naming the analyst, and reviewing statistics about the loaded data as
well as keeping notes about the project.

&ensp;&ensp;&ensp;&ensp;All data-processing parameters are specified on the Project Manager page: whether or not to
normalize counts for SBM; ratio calculation method; preferred index isotope, constraints on weighted means of reference
materials; the common lead model for reference materials; and the physical constants model to be used in the project.
Note on parameter models: See below for how to manage these models from the **Parameters** menu item. Changing these
model selections updates all the dependent calculations within the Squid3 task. The currently-used names and values from
these models are listed in the Expressions Manager left-hand panel and display as read-only expressions. The 'notes'
window in the Expressions manager provides additional info about each model value. Models selected for a task are saved
with the Squid3 Project (.squid) file so if anyone opens a .squid file, the included models will be added to the local
store of models available to Squid3, if not already present.

The **Data** menu: 1) _Manage Sample Names_, 2) _Manage Spots and Reference Materials_, and 3) _Audit Raw Data_ for all data files. For Prawn data files, it also
   provides for saving and swapping out data files.  '_Manage Sample Names_' automates with overrides the naming of
   samples based on delimeters or leading character counts.  '_Manage Spots and Reference Materials_' supports editing
   spot names, filtering spots by name, and setting both the project's reference material spots and concentration
   reference material spots by using the same filtering feature. The reference material models are selected here as
   well. The context menu on the left-side list is accessed by a right-mouse click and has items for removing a spot and
   splitting the data file into two files. Any modifications to the data file can be saved to a new file if needed. Note
   that saving the project preserves the changes to the data within the project file but without changing the source
   data file.  '_Audit masses_' provides time-based views of masses and is configuable to show mass diferences as well.

The **Task** menu - 
1) '_View Current Task_' provides for the user to configure the metadata about a task including
choosing directives for daughter/parent ratios. Additional editing of the task is available via the '_Edit Current
Task_' button at the bottom of the view task page. The task editor provides for editing the masses, the index of the
background mass, the ratios using these masses, the directives, and the four special expressions: **
Uncor_206Pb238U_CalibConst**, **Uncor_208Pb232Th_CalibConst**, **232Th238U_RM**, and **ParentElement_ConcenConst**.
Please note that Squid3 in _GEOCHRON Mode_ requires mass labels 204, 206, 207, 208 and ratios 204/206, 207/206, and
208/206 as they inhabit the built-in expressions engine. In order to use this task with the current data file, the count
of masses must match and Squid3 will alert the user if they do not.  The 'Directives' allow switching between using the primary ages of 206/Pb/238U or 208Pb/232Th
and whether to directly calculate 208Pb/232Th - these are the pair of choices that Squid2.5 also makes available. These
choices interact with the list of preferred index isotopes shown - '208Pb' is only available when the primary age is
206Pb/238U and the calculation of 208Pb/232Th is indirect. Also, if '208Pb' is selected, the directives cannot be
changed.

2) '_New Task from ...' a) current task without custom expressions, b) current task including
custom expressions, c) empty task_ - will each open the task editor. 
3) '_Browse, load, or edit task files ...' a) from
Squid3 Built-In Tasks Library, b) Squid3 Custom Tasks Folder, c) Squid2.5 Custom Tasks Folder_ - each provide access to
a list of tasks with their details, which can then be edited or used directly in the current task, provided the mass
counts align.

The **Isotopes** menu:  1) '_Map Isotopes from Data to Task_' provides for mapping the isotope or mass labels provided
by the data file for each mass staton to the task's isotope or mass labels. Tasks generally refer to masses using
integers such as '204' or simple decimals such as '195.8', whereas the labels for the mass stations recorded in the data
file usually have several significant digits of atomic mass, such as '195.75123'. The background mass can be specified
here or in the task editor. After clicking the orange button at the bottom to copy the labels, the columns D (Data
Isotope Label) and T (Task Isotope Label) will contain the same values.

**Expressions** menu -
'_Manage Expressions_' has a left panel organized by expression type that provides for sorting
the expressions within each list by Name, Execution order, or Target spot set - reference material, concentration
reference material, and unknowns - denoted by left-hand superscripts of ```R or C, and U```. All of the built-in
expressions have been named in a consistent fashion per issue [#164](https://github.com/CIRDLES/Squid/issues/164)
discussions and have explanatory text in their 'notes' panel provided by [@sbodorkos](https://github.com/sbodorkos) . Of
particular note (idea thanks to [@NicoleRayner](https://github.com/NicoleRayner) ) is that the expressions targeting
reference materials only are suffixed by "\_RM". The tab for 'Unhealthy Expressions / Mismatched Targets' will appear in
red ![red](https://placehold.it/15/f03c15/000000?text=+) if any are present. Squid3 supports the assignment of a sample (a group of unknowns) as the target of custom expressions - see the drop down box populated with the sample names to
the right of the unknown samples checkbox. The drop down only appears if unknowns is checked and reference material is
not checked.

&ensp;&ensp;&ensp;&ensp;Squid3 operates under the hood with units of annum and 1-sigma absolute uncertainties as
defaults. Squid3 provides a context for SHRIMP geochronology in the peek window of the expressions manager that displays
ages and their absolute uncertainties in Ma as well as concentrations in ppm, and displays both absolute and percent
uncertainties. Expression grammar has been simplified so that the only times that the surrounding ```'["..."]'```
brackets and quotes are needed is for isotopes, as in ```TotalCPS(["206"])```, for ratios, as in ```["204/206"]```, for
expression names that use other that any combination of letters,digits, and the underscore, and finally for all
expressions where you seek the absolute (±) or percent (%) uncertainty, as in ```[±"204/206"]```. The expression audit
includes an audit of the target spots for compatibility - it checks and reports whether the target choice of Reference
Material and / or Unknowns matches at every level of an expression. Thanks to [@sbodorkos](https://github.com/sbodorkos)
for the suggestion.

**Common Pb** menu - '_Unknowns: 204 Count Corrections & Assign Common Pb Ratios_' provides for customizing how
Squid3 handles common Pb for Unknowns.

**Interpretations** menu - '_Reference Materials_' and '_Unknowns_' provide Wetherill and Tera-Wasserburg
Concordia diagrams produced by [Topsoil](https://github.com/CIRDLES/Topsoil), customizable weighted means with plots,
and a generic plotting function for any two expressions using reference materials.

**Reports** menu - 
1) '_Custom Report Builder_' provides built-in reports for Reference Materials, Unknowns, and
Weighted Means. The report builder supports custom categories with drag and drop columns from the left-side list of
available expressions and drag and drop ordering of categories and columns. Reports can be viewed or saved as '.csv'
files into the project's ```Squid3ProjectReports``` folder that is created in the same folder in which the current
Squid3 project is saved. 
2) '_Summary Expressions and Values_' creates reports for Reference Materials and Unknowns
detailing the summary expressions defined for each. 
3) '_Project Audit_' and '_Task Audit_' produce meta-data reports
for each. 
5) '_Generate all Reports_' produces all reports into the project's ```Squid3ProjectReports``` folder
mentioned above. 
6) '_Miscellaneous Reports_' provides a variety of reports of interest.

**Archiving** menu is not yet active. We seek input from the community as to how archiving might be implemented.

**Parameters** menu - 
'_Reference Material Models_',  '_Common Pb Models_', and '_Physical Constants Models_'
each open the Parameters Manager to the appropriate tab. Squid3 provides several documented default models in each
category. Models can be created, imported from 'xml' files, and exported as 'xml' files. Parameter models used in a
Squid3 project remain with the '.squid' project file so that opening that file provides access to the contained models.
Note that reference material models can be based on TIMS-generated ratios with calculated apparent ages or using a
simplified approach to support the transition from Squid2.5 that provides the requisite "Reference Dates", inspired by
input from [@NicoleRayner](https://github.com/NicoleRayner).

**About** menu - 
1) '_About Squid3_' summarizes Squid3's purpose and identifies collaborators and financial supporters.
2) '_How to cite Squid3_' provides a [link](https://ecat.ga.gov.au/geonetwork/srv/eng/catalog.search#/metadata/133870)
to our latest publication. 
3) '_Squid3 Github Repository_' provides a link to this repository. 
4) '_Squid3 Development
Notes_' links to a [wiki](https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Intro) documenting the porting of Squid2.5 to
Squid3. 
5) '_CIRDLES.org_" links [here](https://cirdles.org/), the home of the College of Charleston development team.
6) '_Topsoil Github Repository_' links to [Topsoil](https://github.com/CIRDLES/Topsoil), used to provide Concordia diagrams in Squid3.

**Help** menu - 
1) '_Introduction and Guide to Menu_' opens this document. 
2) '_Video Tutorials on YouTube_' features 
videos produced by [@NicoleRayner](https://github.com/NicoleRayner) and
found [here](https://www.youtube.com/channel/UCC6iRpem2LkdozahaIphXTg/playlists). 
3) '_Video Tutorials on Google Drive_' features the same
   videos and
   found [here](https://drive.google.com/drive/folders/1PnGhJENKeN6lLJyruc8mGewiUp1DAeCX).
4) '_Contribute an Issue on Github_'
is a shortcut to creating a new issue with key metadata automatically supplied.

Thank you in advance for any suggestions you contribute. We are available for walk-throughs, etc. if needed to help
convince you to participate and contribute.

Additional features will appear over time and may not be documented here concurrently. Please be patient and contribute
your ideas via the issues tracker.

Please volunteer to help with documentation, how-to videos, coding, etc.

Updated 1 November 2021.
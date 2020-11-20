<img src="https://github.com/CIRDLES/DRAKE/blob/master/logos/Squid/SquidLogo.png" width="100">

Squid3
=======

[![Build Status](https://travis-ci.org/CIRDLES/Squid.svg?branch=master)](https://travis-ci.org/CIRDLES/Squid)

Please visit [wiki](https://github.com/CIRDLES/Squid/wiki).

We encourage you to download
and evaluate the latest release, the ".jar" file found <a href="https://github.com/CIRDLES/Squid/releases" target="_blank">here</a>.  
Squid3 requires Oracle's <a href="http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html" target="_blank">Java 1.8</a>.  Java 1.9 and 1.10 are no longer supported and Java 1.11 through 1.17 involve major architectural changes that we have elected to avoid until 2024-2025.  You can read [here](https://en.wikipedia.org/wiki/Java_version_history) about the community's plans for Java.


To contribute issues and feedback, please join GitHub <a href="https://github.com/" target="_blank">here</a>. While using Squid3,
the help menu provides a link that
initializes a new GitHub issue for you - please use freely.

The current versions - Squid3-**1.6.n.jar** - provide full workflow
support for processing and interpreting Shrimp data outputs in the form of 
Prawn xml files and 'OP' files including project, data, task, expressions, visualizations, reports, and parameters management.  Squid3 provides for creating and saving Squid3 Project files that have an
extension of ".squid" and are intended to save the current state of work on a project.  The workflow is mapped left to right
in the menus, as described below.

The **Project** menu provides for creating new _GEOCHRON_ and _RATIO_ projects.  The functionality for _RATIO_ projects is still under development - please feel free to contribute ideas.  A new _GEOCHRON_ project can be created from a single Prawn XML file, from a single Prawn XML file zipped, from merging two Prawn XML files, or from an 'OP' file.  A special demonstration Squid3 project is available from this menu, thanks to [@NicoleRayner](https://github.com/NicoleRayner). The project manager allows for naming the project,
naming the analyst, and reviewing statistics about the loaded data as well as keeping notes about the project.  All data-processing parameters are specified on the Project Manager page: whether or not to normalize counts for SBM; ratio calculation method; preferred index isotope, constraints on weighted means of reference materials; the common lead model for reference materials; and the physical constants model to be used in the project.  Note on parameter models: See below for how to manage these models from the **Parameters** menu item. Changing these model selections updates all the dependent calculations within the Squid3 task.  The currently-used names and values from these models are listed in the Expressions Manager left-hand panel and display as read-only expressions.  The 'notes' window in the Expressions manager provides additional info about each model value.  Models selected for a task are saved with the Squid3 Project (.squid) file so if anyone opens a .squid file, the included models will be added to the local store of models available to Squid3, if not already present.  

The **Data** menu - 1) _Manage Sample Naming_, 
2) _Manage Spots and Reference Materials_, and 3) _Audit Raw Data_ for all data files.  For Prawn data files, it also provides for saving and swapping out data files.  '_Manage Sample Naming_' automates with overrides the naming of samples based on delimeters or leading character counts.  '_Manage Spots and Reference Materials_' supports editing spot names,
filtering spots by name, and setting both the project's reference material spots and 
concentration reference material spots by using the same
filtering feature.  The reference material models are selected here as well.
The context menu on the left-side list is accessed by a
right-mouse click and has items for removing a spot and
splitting the data file into two files.  Any modifications to the data file can be saved
to a new file if needed.  Note that saving the project preserves the changes to the
data within the project file but without changing the source data file.  '_Audit masses_' provides 
time-based views of masses and is configuable to show mass diferences as well.

The **Task** menu - 1) '_View Current Task_' provides for the user to configure the metadata about a task including choosing directives for daughter/parent ratios.  Additional editing of the task is available via the '_Edit Current Task_' button at the bottom of the view task page. The task editor provides for editing the masses, the index of the background mass, the ratios using these masses, the directives, and the four special expressions: **Uncor_206Pb238U_CalibConst**, **Uncor_208Pb232Th_CalibConst**, **232Th238U_RM**, and **ParentElement_ConcenConst**.  Please note that Squid3 in _GEOCHRON Mode_ requires mass labels 204, 206, 207, 208 and ratios 204/206, 207/206, and 208/206 as they inhabit the built-in expressions engine.
The 'Directives' allow switching between using the primary ages of 206/Pb/238U or 208Pb/232Th and whether to directly calculate 208Pb/232Th - these are the pair of choices that Squid2.5 makes available and that we have used to generate and test four possible permutations of these choices.  These choices interact with the list of preferred index isotopes shown - '208Pb' is only available when the primary age is 206Pb/238U and the calculation of 208Pb/232Th is indirect.  Also, if '208Pb' is selected, the directives cannot be changed. In order to use this task with the current data file, the count of masses must match and Squid3 will alert the user if they do not. 2) '_New Task from ...' a) current task without custom expressions, b) current task including custom expressions, c) empty task_ - will each open the task editor. 3) '_Browse, load, or edit task files ...' a) from Squid3 Built-In Tasks Library, b) Squid3 Custom Tasks Folder, c) Squid2.5 Custom Tasks Folder_ - each provide access to a list of tasks with their details, which can then be edited or used directly in the current task, provided the mass counts align.

The **Isotopes & Ratios** menu:  _Manage Isotopes_ provides for mapping the task's isotope or mass labels to the mass stations provided by
the data file.  Tasks generally refer to masses using integers such as '204' or simple decimals such as '195.8', whereas the labels for the mass
stations recorded in the data file usually have several significant digits of atomic mass, such as '195.75123'.  This manager provides the mechanism for mapping one to the other and changing the background mass.  The background mass can also be changed in the task editor.  After clicking the orange button at the bottom to copy the labels, the columns D (Data Isotope Label) and T (Task Isotope Label) will contain the same values.  The last two columns merely toggle whether selected isotope labels are visible in the '_Manage Ratios_' screen  .... **NEEDS WORK**

**Expressions** menu - 1) Manage Expressions has a left panel organized by expression type that provides for sorting the expressions within each list by Name, Execution order, or Target spot set - reference material, concentration reference material, and unknowns - denoted by left-hand superscripts of ```R or C, and U```.  All of the built-in expressions have been named in a consistent fashion per issue [#164](https://github.com/CIRDLES/Squid/issues/164) discussions and have explanatory text in their "notes" panel provided by [@sbodorkos](https://github.com/sbodorkos) .  Of particular note (idea thanks to [@NicoleRayner](https://github.com/NicoleRayner) ) is that the expressions targeting reference materials only are suffixed by "\_RM".  The display font for the expressions and their definitions has been improved and extraneous whitespace removed from the definitions.  The tab for "Unhealthy Expressions / Mismatched Targets" (see below) and will appear in red ![red](https://placehold.it/15/f03c15/000000?text=+) if any are present.  Squid3 now also supports the assignment of a sample - a group of unknowns - as the target of custom expressions - see the drop down box populated with the sample names to the right of the unknown samples checkbox.  The drop down only appears if unknowns is checked and reference material is not checked.

Squid3 operates under the hood with annum and 1-sigma absolute uncertainties as defaults.  We now provide a context for SHRIMP geochronology in the peek window of the expressions manager that displays ages and their absolute uncertainties in Ma as well as concentrations in ppm.  Another improvement is that both absolute and percent uncertainties are now shown in the peeks and tooltips.

The expression-editing workspace has been streamlined.  The "Create empty" button has been re-labeled "Create new" and the editor for new expressions is also now interactively audited as in the case of editing.  The "Copy selected" button has been re-labeled "Create copy".

Expression grammar has been simplified so that the only times that the surrounding '["..."]' brackets and quotes are needed is for isotopes, as in TotalCPS(["206"]), for ratios, as in ["204/206"], for expression names that use other that any combination of letters,digits, and the underscore, and finally for all expressions where you seek the absolute (±) or percent (%) uncertainty, as in [±"204/206"].  Various other enhancements have been provided to assist with expression writing.

The audit now includes an audit of the target spots for compatibility - it checks and reports whether your targets choice of Reference Material and / or Unknowns matches at every level of your expression.  Thanks to @sbodorkos for the suggestion.  Expressions with mismatched targets are shown in the renamed "Unhealthy / Incompatible Target Spot Expressions" tab on the left.  If a subset of unknowns is the target, the sample is also identified.

A new field for "Definition" has been added to functions and operations that will appear in the tooltip for each.  All that remains is to write them!  Any volunteers?

The "peek" area has a new context menu that provides "Copy all" and "Copy all as CSV" and places the contents on your system clipboard.
     
The remaining items in the menu provide for export and import of expressions.

**Common Pb** menu.

**Interpretations** menu - The TW plot has been added for unknowns - the caveat is that the concordia line needs improvement and this is coming soon via the [Topsoil](https://github.com/CIRDLES/Topsoil) project.

**Reports** menu -  The Report Tables now feature a Custom Report for Reference Materials and Unknowns - ``Report Table - per Squid3`` that presents an interactive and sortable live table.  This report is a precursor of our plan to provide a completely configurable report table  similar to the one provided by ET_Redux.  For unknowns there is also a report ready for import into ET_Redux, which was previously released.  The folder containing various user-generated reports - ```Squid3_Reports_vi.j.k``` - is reorganized with inner folders PROJECT / TASK / PRAWN; REPORTS as required.  Executing menu item *Built-In Report Tables / Produce Sanity Check Reports* will fully populate this folder structure.

**Archiving** menu is not yet active.  

**Parameters** menu - This menu replaces the Squid3 Lab Data menu and gives access to the same parameter models.  The parameter models have had several minor improvements including the availability of concentrations in new, empty reference material models.

We have introduced a refinement (inspired by @NicoleRayner ) to the reference material models that provides for creating a model without providing the TIMS-generated ratios and thus calculating the apparent ages.  When a new model is created in the Parameters Manger (use *Edit / Edit new Reference Material Model* menu item), the selection on the right of the "Reference Dates" checkbox will simplify the model to directly accept the ages for the reference material.

**About** menu - '_About Squid3_' summarizes Squid3's purpose and identifies collaborators and financial supporters.  '_How to cite Squid3_' provides a [link](https://ecat.ga.gov.au/geonetwork/srv/eng/catalog.search#/metadata/133870) to our latest publication. '_Squid3 Github Repository_' provides a link to this repository.  '_Squid3 Development Notes_' links to a [wiki](https://github.com/CIRDLES/ET_Redux/wiki/SHRIMP:-Intro) documenting the porting of Squid2.5 to Squid3.  '_CIRDLES.org_" links [here](https://cirdles.org/), the home of the College of Charleston development team.  '_Topsoil Github Repository_' links to [Topsoil](https://github.com/CIRDLES/Topsoil), used to provide Concordia diagrams in Squid3.

**Help** menu - '_Video Tutorials_' features YouTube videos produced by [@NicoleRayner](https://github.com/NicoleRayner) and found [here](https://www.youtube.com/channel/UCC6iRpem2LkdozahaIphXTg/playlists).  '_Contribute an Issue on Github_' is a shortcut to creating a new issue with key metadata automatically supplied.

Thank you in advance for any suggestions you contribute.  We are available for walk-throughs, etc.
if needed to help convince you to participate and contribute.

Additional features will appear over time and may not be documented here concurrently.
Please be patient and contribute your ideas via the issues tracker.

Please volunteer to help with documentation, how-to videos, coding, etc.

Updated 20 November 2020.

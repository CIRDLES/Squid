<img src="https://github.com/CIRDLES/DRAKE/blob/master/logos/Squid/SquidLogo.png" width="100">

Squid 3
=======

[![Build Status](https://travis-ci.org/CIRDLES/Squid.svg?branch=master)](https://travis-ci.org/CIRDLES/Squid)

Please visit [wiki](https://github.com/CIRDLES/Squid/wiki).

We encourage you to download
and evaluate the latest release, the ".jar" file found <a href="https://github.com/CIRDLES/Squid/releases" target="_blank">here</a>.  
Squid requires <a href="http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html" target="_blank">Java 1.8</a>.  Java 1.9 and 1.10 are no longer supported and Java 1.11 and 1.12 involve major architectural changes that we have elected to avoid until 2024-2025.  You can read [here](https://en.wikipedia.org/wiki/Java_version_history) about the community's plans for Java.


To contribute issues and feedback, please join GitHub <a href="https://github.com/" target="_blank">here</a>. While using Squid3,
the help menu provides a link that
initializes a new GitHub issue for you - please use freely.

**Request** The current version is only tested on a few 9-, 10-, and 11-species Prawn xml files.  We would like to have more examples of Prawn files and Squid2.5 tasks in Excel for testing.

The current versions - Squid3-**1.1.n.jar** - provide full workflow
support for processing and interpreting Shrimp data outputs in the form of 
Prawn xml files and 'OP' files including project, data, task, expression, visualizations, report, and parameters management.  Squid provides for creating and saving Squid Project files that have an
extension of ".squid" and are intended to save the current state of work on a project.

The **Project** menu provides for creating a new project from a single Prawn XML file, from merging two Prawn XML files, or from an 'OP' file.   The project manager allows for naming the project,
naming the analyst, and reviewing statistics about the loaded data as well as keeping notes about the project.

The **Data** menu - 1) Manage Spots and Reference Materials, 
2) Manage Sample Naming, and 3) Audit Raw Data for all data files.  For Prawn data files, it also provides for saving and swapping out data files.
  'Manage Spots' supports editing spot names,
filtering spots by name, and setting both the project's reference material spots and 
concentration reference material spots by using the same
filtering feature.  The context menu on the left-side list is accessed by a
right-mouse click and has items for removing a spot and
splitting the data file into two files.  Any modifications to the data file can be saved
to a new file if needed.  Note that saving the project preserves the changes to the
data within the project file but without changing the source data file.  'Manage Sample Naming' automates with overrides the naming of samples based on delimeters or leading character counts.  'Audit masses' provides 
time-based views of masses and is configuable to show mass diferences as well.

The **Task** menu - 1) 'Manage Current Task' provides for the user configure the metadat about a task and to select models for the reference material, concentration reference material, default common lead, and physical constants.  See below for how to manage these models from the 'Resources / Parameter Models' menu item. Changing these model selections in the Task manager immediately updates all the dependent calculations within the Squid3 task.  The currently-used names and values from these models are listed in the Expressions Manager left-hand panel and display as read-only expressions.  The 'notes' window in the Expressions manager provides additional info about each model value.  Models selected for a task are saved with the Squid Project (.squid) file so if anyone opens a .squid file, the included models will be added to the local store of models available to Squid, if not already present.  

Just below the 'Parameters' section are 'Directives'.  Here, the user can switch between primary ages of 206/Pb/238U and 208Pb/232Th and whether to directly calculate 208Pb/232Th - these are the pair of choices that Squid2.5 makes available and that we have used to generate and test four possible permutations of these choices.  Making either choice (by selecting the radio button) immediately re-populates the expressions and re-reduces the data.  These choices interact with the list of preferred index isotopes shown - '208Pb' is only available when the primary age is 206Pb/238U and the calculation of 208Pb/232Th is indirect.  Also, if '208Pb' is selected, the directives cannot be changed.

'Task Designer' and 'New Task from Task Designer' provide for specifying a task's metadata and parameters, including the mass labels (used in the expressions) and the ratios built from the mass labels.  You can start with a blank, import the current task, or use one of the default 9-, 10-, and 11-mass task templates as a starting point.  Please note that Squid3 requires mass labels 204, 206, 207, 208 and ratios 204/206, 207/206, and 208/206 as they inhabit the built-in expressions engine.  Also, if you choose to label a mass station as background, the add/remove button accepts the ordinal position (1, 2, 3...) and automatically labels the mass 'BKG'.  Custom mass labels and ratios built from them can be removed by clicking and restored by using the restore menu in the appropriate "add/remove" button.  The directives are configurable here as well with the added feature that you can enter, edit, and audit the four special expressions of Squid used to drive the analysis permutations: **Uncor_206Pb238U_CalibConst, Uncor_208Pb232Th_CalibConst, 232Th238U_RM, and ParentElement_ConcenConst.** . The expressions at play in each permutation are highlighted with a red box.  If you leave the expressions in a "failed to parse" state, you will be able to edit them in the expressions manager (see below) once you create a task from the task designer - this feedback here is for insight and assistance as needed.  Once you have the task organized, use the menu item 'New Squid Task from Task Designer' to create the task - you will be informed if there is a mismatch between the mass labels count in the task designer and the data file.  Your next step is the usual Isotope Manager and the expressions manager, where you will find there are no custom expressions!  This is where you might import from a folder of expressions you have previously saved, or write new ones.

**Expressions** menu - 1) Manage Expressions has a simplified left panel that provides for sorting the expressions within each list by Name, Execution order, or Target spot set - reference material, concentration reference material, and unknowns - denoted by left-hand superscripts of ```R or C, and U```.  All of the built-in expressions have been renamed in a consistent fashion per issue #164 discussions and have explanatory text in their "notes" panel provided by @sbodorkos .  Of particular note (idea thanks to @NicoleRayner ) is that the expressions targeting reference materials only are now suffixed by "\_RM" and the previously-used suffix of "S" for expressions targeting unknowns only has been removed.  The display font for the expressions and their definitions has been improved and extraneous whitespace removed from the definitions.  The tab for "Unhealthy Expressions" has been re-labelled "Unhealthy Expressions / Mismatched Targets" (see below) and will appear in red ![red](https://placehold.it/15/f03c15/000000?text=+) if any are present.

Squid3 now also supports the assignment of a group of unknowns as the target of custom expressions - see the drop down box populated with the sample names to the right of the unknown samples checkbox.  This is in preparation for the final phase of the Squid2.5 data reduction translation by @sbodorkos , coming soon.  The drop down only appears if unknowns is checked and reference material is not checked.

Squid 3 operates under the hood with annum and 1-sigma absolute uncertainties as defaults.  We now provide a context for SHRIMP geochronology in the peek window of the expressions manager that displays ages and their absolute uncertainties in Ma as well as concentrations in ppm.  Another improvement is that both absolute and percent uncertainties are now shown in the peeks and tooltips.

The expression-editing workspace has been streamlined.  The "Create empty" button has been re-labeled "Create new" and the editor for new expressions is also now interactively audited as in the case of editing.  The "Copy selected" button has been re-labeled "Create copy".

Expression grammar has been simplified so that the only times that the surrounding '["..."]' brackets and quotes are needed is for isotopes, as in TotalCPS(["206"]), for ratios, as in ["204/206"], for expression names that use other that any combination of letters,digits, and the underscore, and finally for all expressions where you seek the absolute (±) or percent (%) uncertainty, as in [±"204/206"].  Various other enhancements have been provided to assist with expression writing.

The audit now includes an audit of the target spots for compatibility - it checks and reports whether your targets choice of Reference Material and / or Unknowns matches at every level of your expression.  Thanks to @sbodorkos for the suggestion.  Expressions with mismatched targets are shown in the renamed "Unhealthy / Incompatible Target Spot Expressions" tab on the left.  If a subset of unknowns is the target, the sample is also identified.

A new field for "Definition" has been added to functions and operations that will appear in the tooltip for each.  All that remains is to write them!  Any volunteers?

The "peek" area has a new context menu that provides "Copy all" and "Copy all as CSV" and places the contents on your system clipboard.
     
The remaining items in the menu provide for export and import of expressions.

**Report Tables** menu -  The Report Tables now feature a Custom Report for Reference Materials and Unknowns - ``Report Table - per Squid 3`` that presents an interactive and sortable live table.  This report is a precursor of our plan to provide a completely configurable report table  similar to the one provided by ET_Redux.  For unknowns there is also a report ready for import into ET_Redux, which was previously released.  The folder containing various user-generated reports - ```Squid3_Reports_vi.j.k``` - is reorganized with inner folders PROJECT / TASK / PRAWN; REPORTS as required.  Executing menu item *Built-In Report Tables / Produce Sanity Check Reports* will fully populate this folder structure.

**Visualizations** menu - The TW plot has been added for unknowns - the caveat is that the concordia line needs improvement and this is coming soon via the [Topsoil](https://github.com/CIRDLES/Topsoil) project.

**Resources** menu - This menu replaces the Squid Lab Data menu and gives access to the same parameter models.  The parameter models have had several minor improvements including the availability of concentrations in new, empty reference material models.

We have introduced a refinement (inspired by @NicoleRayner ) to the reference material models that provides for creating a model without providing the TIMS-generated ratios and thus calculating the apparent ages.  When a new model is created in the Parameters Manger (use *Edit / Edit new Reference Material Model* menu item), the selection on the right of the "Reference Dates" checkbox will simplify the model to directly accept the ages for the reference material.

Thank you in advance for any suggestions you contribute.  We are available for walk-throughs, etc.
if needed to help convince you to participate and contribute.

Additional features will appear over time and may not be documented here concurrently.
Please be patient and contribute your ideas via the issues tracker.

Please volunteer to help with documentation, how-to videos, coding, etc.

Updated 5 March 2019

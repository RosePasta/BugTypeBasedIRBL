### Prepare the Bench4BL
- https://github.com/exatoa/Bench4BL
- J. Lee, et al. Bench4bl: reproducibility study on the performance of ir-based bug localization, ISSTA'18

### ./config
- Bench4BL = path of original bench4bl dataset
- output = path to save the new bug repository

### datagen.core.bench4bl.DataGen.java
- Create a new bug repository by extracting the buggy methods based on the bug repositories provided by Bench4BL
- Classify the source files into production and test files, and create index files using Lucene

#### ./bench4bl/spring/shdp/
- sample data for testing
- bugrepo: original bug repository from the Bench4BL
- sources: original source file collection from the Bench4BL
- gitrepo: git repository

#### ./output/spring/shdp/
- output data from the DataGen.java
- bug: new bug repository
- sources: new source file collection having already index files

### irbl.core.IRBL.java
- based on new bug repository generated by DataGen.java
- Evaluate the performance of the IRBL based on Lucene
- Evaluation options: original, correct ground-truth files (GTF), production bug localization (PBL), test bug localization (TBL)

### Our Research Results
- https://drive.google.com/open?id=1AKC9sydf2-IiGEeZB94Mx42qsNJaDw1Z
- ./Tables/ : All investigation and experimental results
- 1_Bench4BL_bugs.Method.zip: new bug repository extracted by DataGen.java
- 2_Bench4BL_bugs_Type.zip: a set of pairs with bug id and bug type
- 3_Exp_Results_Tools.zip: All detail experimental results for six tools (we used these tools from the Bench4BL)
   - BugLocator: J.Zhou, et al. Where should the bugs be fixed? more accurate information retrieval-based bug localization based on bug reports, ICSE'12
   - BLUiR: R.K.Saha, Improving bug localization using structured information retrieval, ASE'13
   - BRTracer: C.P.Wong, et al. Boosting bug-report-oriented fault localization with segmentation and stack-trace analysis, ICSME'14
   - AmaLgam: S.Wang, Version history, similar report, and structure: Putting them together for improved bug localization, ICPC'14
   - BLIA: K.C. Youm, et al. Bug localization based on code change histories and bug reports, APSEC'15
   - LOCUS: M.Wen, et al. Locus: Locating bugs from software changes, ASE'16

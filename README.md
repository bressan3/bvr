### About
The Base Variability Resolution (BVR) is a language to engineer software product lines. The BVR tool bundle is a set of plug-ins for Eclipse that implements and supports the BVR language. The bundle enables feature modeling, resolution, realization and derivation of products, their testing and analysis.

### Downloads and Installation
See https://bvr-tool.sintef.cloud

### How to Run/Debug:
1. Clone the repo:

```
cd [path to your eclipse workspace]
git clone https://github.com/bressan3/bvr.git
```

2. Open Eclipse (OSATE2 preferably)
3. Go to `File -> Open Projects from File System...`
4. Select the cloned bvr folder
5. Select all the folders that will show up on the list except the `bvr` folder
5. On Eclipse's navigator, right click on any of the imported folders and then `Debug As -> Eclipse Application`
6. A new instance of Eclipse, now containing the BVR extension, will launch

In order to make sure that BVR is actually working, go to `File -> New Project`. Look up `BVR`. If `BVR Model` comes up, it means that the debug version of Eclipse you're running contains BVR.

### Editor Adapter
BVR's Realization Editor can be extended to work with other graphical tools based on EMF. This can be done by creating a new BVR Editor Adapter.

The default Editor Adapter should be located under `bvr/no.sintef.bvr.papyrusdiagram.adapter/src/no/sintef/bvr/papyrusdiagram/adapter/PapyrusBVREditorAdapter.java`

### Source Installation Requirements
1. Eclipse Modeling Tools - BVR was developed on eclipse Kepler SR2, but also seems to work on eclipse Neon
2. Papyrus UML v0.10.1 for third-party integration plugins.  BVR seems to work fine using the Papyrus Neon release (2.0.3)
3. Java 8

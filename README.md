<p align="center">
  <a href="" rel="noopener">
 <img src="doc/img/dino_logo.jpg" alt="Dino logo"></a>
</p>
<h3 align="center">Dino In SpinalHDL</h3>

<div align="center">

[![Status](https://img.shields.io/badge/status-active-success.svg)]()
[![GitHub Issues](https://img.shields.io/github/issues/kylelobo/The-Documentation-Compendium.svg)](https://github.com/0xtaruhi/Dino/issues)
[![GitHub Pull Requests](https://img.shields.io/github/issues-pr/kylelobo/The-Documentation-Compendium.svg)](https://github.com/0xtaruhi/Dino/pulls)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE.md)

</div>

---

<p align="center"> Chrome Dino game in SpinalHDL
    <br> 
</p>

## 📝 Table of Contents

- [📝 Table of Contents](#-table-of-contents)
- [🏁 Getting Started ](#-getting-started-)
  - [Prerequisites](#prerequisites)
  - [Generating the verilog](#generating-the-verilog)
- [⛏️ Built With ](#️-built-with-)
- [✍️ Author ](#️-author-)

## 🏁 Getting Started <a name = "getting_started"></a>

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- sbt
  - [Install sbt](https://www.scala-sbt.org/1.x/docs/Setup.html)
- SpinalHDL
  - [Getting started - SpinalHDL](https://spinalhdl.github.io/SpinalDoc-RTD/master/SpinalHDL/Getting%20Started/index.html)

### Generating the verilog

run the following command in the root directory of the project.

```bash
sbt "runMain VerilogEmit"
```

The generated verilog file will be in the `hw/spinal/generated` directory.

## ⛏️ Built With <a name = "tech_stack"></a>

- [SpinalHDL](https://spinalhdl.github.io/SpinalDoc-RTD/) - SpinalHDL is a Hardware Description Language (HDL) embedded in Scala

## ✍️ Author <a name = "author"></a>

- [@0xtaruhi](https://github.com/0xtaruhi) - Idea & Initial work

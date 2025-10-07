# 《Redis 4.x Cookbook》Learning Notes



## 1. Profiles

![Redis 4.x Cookbook](assets/cover.png)

|    **Title**    | **Redis 4.x Cookbook** [ISBN: 9781783988167] |
| :-------------: | :----------------------------------------------------------: |
|   **Author**    |               **Pengcheng Huang, Zuofei Wang**               |
| **Publication** |                      **Packt, 2018.2**                       |
|    **Pages**    |                           **374**                            |

> **Introduction**
>
> JUnit is the gold standard for unit testing Java applications. Filled with powerful new features designed to automate software testing, JUnit 5 boosts your productivity and helps avoid debugging nightmares. Whether you're just starting with JUnit or you want to ramp up on the new features, *JUnit in Action, Third Edition* has you covered. Extensively revised with new code and new chapters, *JUnit in Action, Third Edition* is an up-to-date guide to smooth software testing. Dozens of hands-on examples illustrate JUnit 5's innovations for dependency injection, nested testing, parameterized tests, and more. Throughout, you’ll learn how to use JUnit 5 to automate your testing, for a process that consumes less resources, and gives you more time for developing.
>
> ## about the technology
>
> The JUnit framework is the gold standard for unit testing Java applications—and knowing it is an essential skill for Java developers. The latest version, JUnit 5, is a total overhaul, now supporting modern Java features like Lambdas and Streams.
>
> ## about the book
>
> *JUnit in Action, Third Edition* has been completely rewritten for this release. The book is full of examples that demonstrate JUnit's modern features, including its new architecture; nested, tagged, and dynamic tests; and dependency injection. You'll benefit from author Cătălin Tudose's unique "pyramid" testing strategy, which breaks the testing process into layers and sets you on the path to bug-free code creation.



## 2. Outlines

Status available：:heavy_check_mark: (Completed) | :hourglass_flowing_sand: (Working) | :no_entry: (Not Started) | :orange_book: (Finish reading)

| No.  |                        Chapter Title                         |       Status       |
| :--: | :----------------------------------------------------------: | :----------------: |
|      |                       **Part I JUnit**                       |                    |
| Ch01 |     [JUnit jump-start](./notes/Ch01_JUnit_jumpstart.md)      | :heavy_check_mark: |
| Ch02 | [Exploring core JUnit](./notes/Ch02_exploring_core_JUnit.md) |   :orange_book:    |
| Ch03 |   [JUnit architecture](./notes/Ch03_JUnit_architecture.md)   |   :orange_book:    |



Powershell script for generating markdown files in batch:

```powershell
# Create 22 empty markdown files named Ch##.md:
for($i=1; $i -le 13; $i=$i+1){ New-Item -Name "Ch$('{0:d2}' -f $i).md"; }
```

 

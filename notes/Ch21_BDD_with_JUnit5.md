# 第二十一章：用 JUnit 5 进行行为驱动开发



> **本章概要**
>
> - 解析 BDD 的优势与面临的挑战；
> - 将 TDD 应用迁移至 BDD 模式下的方法；
> - 用 Cucumber 与 JUnit 5 开发 BDD 应用的方法；
> - 用 JBehave 与 JUnit 5 开发 BDD 应用的方法。

> *Some people refer to BDD as “TDD done right.” You can also think of BDD as “how we build the right thing” and TDD as “how we build the thing right.”*
> 有人认为 BDD 是“用对了的 TDD”。你也可以将 BDD 视为“如何构建正确的东西”；而 TDD 则是“如何正确地构建东西”。
>
> —— **Millard Ellingsworth**

本章内容看似很多，其实只触及 `BDD` 的冰山一角。初学时因为 `IDEA` 一个插件报错耽搁了很久。若想深入了解 `BDD`，可以参考 `Manning` 出版社 2023 年 3 月出版的《*BDD in Action*》第二版。本章只能算开胃菜，让纯小白熟悉 `BDD` 的基础玩法。



## 21.1 TDD 暴露的问题

主要体现在三个方面：

- 容易一叶障目：`TDD` 的常规流程（测试 :arrow_right: 编码 :arrow_right: 重构 :arrow_right: ……（循环往复））很可能让人只见树木、不见森林；单元测试通常只关注 `SUT`（被测对象，即某个类或方法），容易忽略业务目标；
- 大型项目运维困难：实际工作场景将比演示案例复杂很多，最终导致测试难以理解和维护；
- 牵一发动全身：测试用例通常与应用实现的细节高度耦合。



## 21.2 BDD 简介




































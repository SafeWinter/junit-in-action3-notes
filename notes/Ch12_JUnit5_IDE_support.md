# 第十二章：JUnit 5 与主流 IDE 的集成



> **本章概要**
>
> - `JUnit 5` 与 `IntelliJ IDEA` 的集成
> - `JUnit 5` 与 `Eclipse` 的集成
> - `JUnit 5` 与 `NetBeans` 的集成

本章按照 `JUnit 5` 团队与主流 `IDE` 集成的先后顺序，依次演示了同一套测试用例在 `IntelliJ IDEA`、`Eclipse` 以及 `NetBeans` 中的运行过程。

由于 `IDEA` 集成最早、最成熟，各项 `JUnit 5` 特性都实现得很好（强烈推荐）。

`Eclipse` 的集成稍晚，但由于不能手动强制执行添加了 `@Disabled` 注解的测试类，功能稍微偏弱。

`NetBeans` 的集成最晚，市场份额也最小，部分功能特性需要安装 `JaCoCo` 插件才能达到在 `IDEA` 中的效果，不太推荐。

三款 `IDE` 集成 `JUnit 5` 的横向对比情况如下：

|              特性              |   IntelliJ IDEA    |      Eclipse       |         NetBeans         |
| :----------------------------: | :----------------: | :----------------: | :----------------------: |
|   强制运行 `@Disabled` 测试    | :heavy_check_mark: |        :x:         |           :x:            |
|    `@DisplayName` 渲染良好     | :heavy_check_mark: | :heavy_check_mark: |           :x:            |
| 基于 `@TestFactory` 的动态测试 | :heavy_check_mark: | :heavy_check_mark: |           :x:            |
|           `@Nested`            | :heavy_check_mark: | :heavy_check_mark: |      不执行嵌套测试      |
|      `@ParameterizedTest`      | :heavy_check_mark: | :heavy_check_mark: |           :x:            |
|        `@RepeatedTest`         | :heavy_check_mark: | :heavy_check_mark: |           :x:            |
|        带分类标记的测试        | :heavy_check_mark: | :heavy_check_mark: | 需要 `Surefire` 插件辅助 |
|     运行带测试覆盖率的测试     | :heavy_check_mark: | :heavy_check_mark: |  需要 `JaCoCo` 插件辅助  |


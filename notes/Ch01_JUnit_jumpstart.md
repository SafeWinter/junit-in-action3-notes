# 第一章 JUnit 起步

本章快速介绍测试的基本概念，同时也是本书必备的入门知识。



> *Never in the field of software development was so much owed by so many to so few lines of code.*
> 在软件开发领域，还从未有如此少的代码量能对如此多的人产生如此重大的影响。
>
> —— Martin Fowler



## 1.1 开发者的分类

基于是否熟练掌握测试技能，可将开发者大致分类两类——

- 纯手动测试型（"play-test" developer）
- 测试爱好者型（the test-infected）



## 1.2 JUnit 的历史

1997 年，**Erich Gamma** 和 **Kent Beck** 合作开发了 `JUnit`。当时 **Erich** 希望 **Kent** 尝试 `Java`，并对 **Kent** 早年间为 `Smalltalk` 设计的 `SUnit` 测试框架充满兴趣。**Erich** 是四人帮（`GOF`）成员之一，而 **Kent** 则因开创了 [极限编程](http://www.extremeprogramming.org/) 而享誉业内。

`JUnit` 之于 `Java` 单元测试，相当于 `MS Excel` 之于办公软件，都是各自领域内的事实标准。

`JUnit` 是托管到 `GitHub` 的开源软件，基于 `Eclipse` 公共许可（`Eclipse` Public License）。其底层测试模型 `xUnit` 正逐步成为其它多种语言的标准框架，如 `ASP`、`C++`、`C#`、`Eiffel`、`Delphi`、`Perl`、`PHP`、`Python`、`Rebol`、`Smalltalk` 以及 `Visual Basic`。



## 1.3 与单元测试相关的基本概念

本书中 **单元测试** 的含义界定（更狭义）：检验独立工作单元行为的测试（也称为 **开发者测试**），即确认某方法能接受预期范围的输入，并针对每个输入返回预期的结果。
这里的 **工作单元**，应该是不直接依赖其他任务完成情况的某类任务，这在 `Java` 应用中通常表现为单个方法。

单元测试通常关注的是验证方法是否遵循其 **API 契约** 的条款
这里的 **API 契约**，是将应用程序接口（即 API）视为调用方与被调用方之间正式协议的一种观点，其概念源于 `Eiffel` 兴起的契约式设计实践（Design by Contract）。
单元测试通过 **展示预期行为** 来定义 API 契约。

单元测试的核心原则：没有自动化测试的功能特性等同于不存在该特性（Any program feature without an automated test simply doesn’t exist.）。



## 1.4 从 add 方法开始

以单元测试领域的 `Hello World` 开始：

```java
public class Calculator {
    public double add(double number1, double number2) {
        return number1 + number2;
    }
}
```

最简单的测试逻辑也最直观：

```java
public class CalculatorTest {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        double result = calculator.add(10, 50);
        if (result != 60) {
            System.out.println("Bad result: " + result);
        }
    }
}
```

缺陷：测试是否通过还得仔细阅读输出结果，对比脑海中的正确结果，**本质上还是手动测试**。

要体现某测试不通过，最好是以抛异常的方式进行描述。一个更好的测试用例应该通过 **模块化** 的设计来捕获、处理异常：

```java
public class CalculatorTest {
    private int nbErrors = 0;
    public void testAdd() {
        Calculator calculator = new Calculator();
        double result = calculator.add(10, 50);
        if (result != 60) {
            throw new IllegalStateException("Bad result: " + result);
        }
    }
    public static void main(String[] args) {
        CalculatorTest test = new CalculatorTest();
        try {
            test.testAdd();
        }
        catch (Throwable e) { 2
            test.nbErrors++;
            e.printStackTrace();
        }
        if (test.nbErrors > 0) {
            throw new IllegalStateException("There were " + test.nbErrors + " error(s)");
        }
    }
}
```

这里的 `testAdd()` 方法就是一个模块。



## 1.5 测试框架必须具备的三个目标

1. 帮助人们写出有效测试；
2. 写出的测试可以保值；
3. 必须能通过代码复用降低编写成本。



## 1.6 JUnit 的入门设置

`JUnit 5` 高度依赖注解，让基类去检测所有测试类上的测试方法，对形如 `testXYZ` 型的方法名执行统一的测试。

本书使用 `Maven` 对 `JUnit` 框架的相关依赖进行管理。最开始需要的两个依赖为 `junit-jupiter-api` 和 `junit-jupiter-engine`：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.6.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.6.0</version>
    <scope>test</scope>
</dependency>
```

为了能从命令行运行测试，还需要配置 `Maven` 插件 `maven-surefire-plugin`：

```xml
<build>
    <plugins>
        <plugin>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>2.22.2</version>
        </plugin>
    </plugins>
</build>
```

另外 `Java` 版本不得低于 `Java 8`。由于我本地实测用的 `Java 11`，`pom.xml` 中还需要手动指定示例项目的 `Java` 编译版本：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
        <source>11</source>
        <target>11</target>
    </configuration>
</plugin>
```

> [!tip]
>
> 由于示例源码已根据章节划分为不同的项目，在 `IDEA` 中直接以章节文件夹为根目录打开项目即可。



## 1.7 第一个 JUnit 单元测试

先上代码：

```java
package com.manning.junitbook.ch01;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class CalculatorTest {

    @Test
    public void testAdd() {
        Calculator calculator = new Calculator();
        double result = calculator.add(10, 50);
        assertEquals(60, result, 0);
    }
}
```

重要补充：这里的断言方法 `assertEquals()` 支持三个参数：期望值、实际值、最大误差。具体签名如下：

```java
/**
 * <em>Assert</em> that {@code expected} and {@code actual} are equal within the given non-negative {@code delta}.
 * <p>Equality imposed by this method is consistent with {@link Double#equals(Object)} and
 * {@link Double#compare(double, double)}.
 */
public static void assertEquals(double expected, double actual, double delta) {
  AssertEquals.assertEquals(expected, actual, delta);
}
```

运行测试：

```powershell
> mvn test -Dtest=CalculatorTest
```

注意：`-Dtest` 的值可以是下列情况之一：

1. 要运行的测试类名（`CalculatorTest`）；
2. 测试类的文件名（`"CalculatorTest.java"`）；
3. 某个包路下的所有测试（`"com/manning/junitbook/ch02/disabled/*"`）。



> [!tip]
>
> **【小贴士】关于用 `-D` 表示定义一个变量的写法**
>
> 该写法最早 **继承自 C/C++ 编译器的传统**。在 `Java` 诞生之前，`C` 和 `C++` 的编译器（如 `gcc`, `cc`）就已经广泛使用 `-D` 来 **定义宏**：
>
> ```bash
> gcc -DDEBUG -DVERSION=1.0 myprogram.c
> ```
>
> 上述命令将在编译时定义了一个 `DEBUG` 宏（值为1）和一个 `VERSION` 宏（值为1.0）。
>
> 当 `Java` 在 1990 年代中期被创建时，它的设计者 **James Gosling** 本身是 `C`/`C++` 领域的专家。为了让 `Java` 对开发者显得熟悉和友好，有意借鉴了 `C`/`C++` 编译器的命令行选项风格。因此，用 `-D` 来表示 **定义** 某个值。
>
> 另外，单字母命令行选项在 `Unix` 世界中是标准做法。这些选项通常（但并非总是）是其所代表单词的 **首字母**，例如：
>
> - `-v` :arrow_right: `verbose`
> - `-f` :arrow_right: `file`
> - `-D` :arrow_right: `Define`
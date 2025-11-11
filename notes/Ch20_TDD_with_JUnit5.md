# 第五部分：用 JUnit 5 开发应用程序

这一部分内容重点探讨如何将 `JUnit 5` 融入现代项目的日常开发工作——

第 20 章探讨如何使用当下流行的测试驱动开发（TDD）技术进行项目开发。

第 21 章探讨行为驱动开发（BDD）模式实施项目的具体方法。并给出完整开发案例。

第 22 章将借助 `JUnit 5` 构建测试金字塔策略（test pyramid strategy）：从底层（单元测试）到上层（集成测试、系统测试和验收测试）的全方位测试方法。



# 第二十章：用 JUnit 5 进行测试驱动开发



> **本章概要**
>
> - 普通项目改造为 `TDD` 项目的方法；
> - `TDD` 应用的重构方法；
> - `TDD` 在实现新功能特性中的应用。

> *TDD helps you to pay attention to the right issues at the right time so you can make your designs cleaner, you can refine your designs as you learn. TDD enables you to gain confidence in the code over time.*
> 测试驱动开发（`TDD`）有助于在正确的时间关注正确的问题，从而使设计更加清晰，并能在学习过程中不断优化设计。随着时间的推移 `TDD` 能让开发者建立起对代码的信心。

本章没有堆砌 `TDD` 的观点理论，而是真正从实战角度出发，手把手地带领大家见识见识，究竟什么才是真正的测试驱动开发流程。



## 20.1 TDD 核心概念简介

**测试驱动开发（Test-driven development）** 是一种编程实践，它采用短周期循环的开发模式：先将需求转化为测试用例，再修改程序代码使测试通过。

`TDD` 倡导简约设计并强调安全性：其核心理念是追求 **可运行的简洁代码（clean code that works）**。

`TDD` 的三个优势：

- 代码由明确的目标驱动，确保精准满足应用需求；
- 引入新功能的速度显著提升：既能更快实现新功能，又能大幅降低出 Bug 的可能；
- 测试用例就能充当应用的文档（离不开对项目需求的深刻理解和前期良好的设计）。

`TDD` 实施的好坏还跟代码 **重构** 的质量密切相关。

**重构（*Refactoring*）** 就是在不影响软件系统外部行为的前提下改善其内部结构的过程。



## 20.2 实战1：初始项目的搭建

即搭建一个非 `TDD` 的项目作为演示的起点。还是以航班管理应用为例，具体需求如下：

在航班 `Flight` 与 `Passenger` 乘客实体交互过程中，航班添加乘客的规定如下 ——

- 经济舱航班可供 `VIP` 乘客及普通乘客搭乘；
- 商务舱航班仅供 `VIP` 乘客搭乘。

航班移除乘客的规定如下——

- 普通乘客可被移除；
- `VIP` 乘客不可移除。

具体示意图如下：

![](../assets/20.1.png)

![](../assets/20.2.png)

由此确定 `Flight` 与 `Passenger` 的初始设计：航班实体具有 `String` 型字段 `flightType`，`Passenger` 也是航班实体的一个集合字段，具体类图如下：

![](../assets/20.3.png)

根据需求定义 `Passenger` 实体类如下：

```java
public class Passenger {

    private String name;
    private boolean vip;

    public Passenger(String name, boolean vip) {
        this.name = name;
        this.vip = vip;
    }

    public String getName() {
        return name;
    }

    public boolean isVip() {
        return vip;
    }
}
```

航班实体类 `Flight` 如下：

```java
public class Flight {

    private String id;
    private List<Passenger> passengers = new ArrayList<Passenger>();
    private String flightType;

    public Flight(String id, String flightType) {
        this.id = id;
        this.flightType = flightType;
    }

    public String getId() {
        return id;
    }

    public List<Passenger> getPassengersList() {
        return Collections.unmodifiableList(passengers);
    }

    public String getFlightType() {
        return flightType;
    }

    public boolean addPassenger(Passenger passenger) {
        switch (flightType) {
            case "Economy":
                return passengers.add(passenger);
            case "Business":
                if (passenger.isVip()) {
                    return passengers.add(passenger);
                }
                return false;
            default:
                throw new RuntimeException("Unknown type: " + flightType);
        }

    }

    public boolean removePassenger(Passenger passenger) {
        switch (flightType) {
            case "Economy":
                if (!passenger.isVip()) {
                    return passengers.remove(passenger);
                }
                return false;
            case "Business":
                return false;
            default:
                throw new RuntimeException("Unknown type: " + flightType);
        }
    }
}
```

由于是非 `TDD` 项目，测试用例也最好不用 `JUnit`，于是新建一个 `Airport` 类模拟手动测试逻辑：

```java
public class Airport {

    public static void main(String[] args) {
        Flight economyFlight = new Flight("1", "Economy");
        Flight businessFlight = new Flight("2", "Business");

        Passenger james = new Passenger("James", true);
        Passenger mike = new Passenger("Mike", false);

        businessFlight.addPassenger(james);
        businessFlight.removePassenger(james);
        businessFlight.addPassenger(mike);
        economyFlight.addPassenger(mike);

        System.out.println("Business flight passengers list:");
        for (Passenger passenger : businessFlight.getPassengersList()) {
            System.out.println(passenger.getName());
        }

        System.out.println("Economy flight passengers list:");
        for (Passenger passenger : economyFlight.getPassengersList()) {
            System.out.println(passenger.getName());
        }
    }
}
```

运行 `main` 方法（符合预期）：

![](../assets/20.4.png)



## 20.3 实战2：改造为带单元测试的准 TDD 版





## 20.4 实战3：项目重构的正确打开方式





## 20.5 实战4：添加新特性——新的航班类型





## 20.6 实战5：添加新特性——每位乘客只能被添加一次




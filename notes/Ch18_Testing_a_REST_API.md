# 第十八章：REST API 测试



> **本章概要**
>
> - 用 `RESTful API` 管理一个或多个实体的方法；
> - `RESTful API` 的测试方法。



> *For now, let's just say that if your API is re-defining the HTTP verbs or if it is assigning new meanings to HTTP status codes or making up its own status codes, it is not RESTful.*
> 此刻，如果你的 API 重新定义了 HTTP 动词，或是赋予了 HTTP 状态码新的含义，甚至自行创建了状态码，那么它就不符合 RESTful 规范。
>
> —— **George Reese**, *The REST API Design Handbook*



## 18.1 REST 应用概述

`REST` 的全称为 `REpresentational State Transfer`，中文译为 “**表述性状态转移**”，由美国计算机科学家 **Roy Fielding** 于 2000 年发表的博士论文中首次定义。

本质上看，`REST` 是一种 **软件架构风格**：它为一组网络组件（客户端和服务器）如何相互通信设计了一组约束和原则。它 **并非** 某个标准或协议，而是一种软件 **设计哲学**。

当某个 `Web Service` 服务遵循了 `REST` 的基本原则时，就称其为 **RESTful API** 接口，或者具备 **RESTful** 风格的 `Web Service` 服务。

具备 `RESTful` 风格的 `Web Service` 服务实现了 `Web` 端与计算机系统间的互操作，其请求系统可以通过一组明确的无状态操作（stateless operations）来访问和操作以 **文本形式** 呈现的网络资源。

`RESTful API` 的两个构成要素：

- **客户端**：使用该接口的人或软件（如浏览器）；
- **资源**：能够用 `API` 接口获取信息的任何对象，均可视为该接口的某种资源（如消息、用户信息、图片等）；每个资源都有一个唯一标识（identifier）。



## 18.2 REST 风格的六大约束

`REST` 架构风格定义的六大约束：

1. **客户端/服务器模式（Client-server）**：客户端与服务器相互分离，各自关注不同的层面：
   1. 客户端关注用户界面呈现效果；
   2. 服务器关注数据存储及域模型逻辑（及包含数据和行为的领域概念模型）
2. **无状态性（Stateless）**：服务器不会在请求间保留客户端任何信息，状态由客户端自行维护；
3. **接口统一（Uniform interface）**：只要接口不变，客户端与服务器可以独立升级演进，接口统一使其保持松耦合关系。
4. **分层设计的系统（Layered systems）**：客户端无法判定其是否直接与服务端还是中间层在交互，每一层均可动态添加或移除，并基于分层设计提供安全、负载均衡、共享缓存等特性；
5. **缓存设计（Cacheable）**：客户端可以对响应进行缓存，响应本身也可以自行声明能否被缓存；
6. **按需定制（Code on demand）**：非必选项。服务器可以临时定制或扩展客户端的功能特性，如向客户端传输某些执行逻辑（`JavaScript` 脚本或 `Java applet` 逻辑）

虽然 `RESTful` 风格的 `API` 接口并不限定通信协议，但应用最广泛的还是基于 `HTTP` 协议的 `REST API` 接口。

客户端发起请求，会收到服务器返回的响应内容，这些内容代表了被请求资源的 **某种状态**。状态的具体表现格式主要有 `JSON`、`XML`、`HTML` 格式。

发送到服务端的具体内容主要由两部分构成：

- 目标资源的 `URL`；
- 希望服务器对该资源执行的某种操作，即 `HTTP` 方法（如 `GET`、`POST`、`PUT`、`PATCH`、`DELETE`）。



## 18.3 RESTful API 接口的创建

首先添加三个 `Maven` 依赖：

- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `h2`

处于实战考虑，我本地基于最新的 `SpringBoot Initializr` 生成的默认项目进行扩充。`IntelliJ IDEA` 在 `pom.xml` 文件提供了便捷的动态管理入口：

![](../assets/18.1.png)

![](../assets/18.2.png)

本例将创建一个对国家实体类 `Country` 提供 `CRUD` 基本操作的 `REST API` 接口。该接口的基础 `URL` 为 `http://localhost:8081/`，端口 `8081` 通过项目配置文件 `src/main/resources/application.properties` 指定（第一行是 `Initializr` 的默认配置）：

```properties
spring.application.name=spring-boot
server.port=8081
```

国家数据的初始化拟通过另一个 `CSV` 文件 `src/main/resources/countries_information.csv` 批量导入 `H2` 数据库：

```markdown
Australia; AU
USA; US
United Kingdom; UK
```

具体的导入逻辑通过修改 `FlightBuilder` 类实现：

```java
public class FlightBuilder {

    private final Map<String, Country> countriesMap = new HashMap<>();

    public FlightBuilder() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/countries_information.csv"))) {
            String line;
            do {
                line = reader.readLine();
                if (line != null) {
                    String[] countriesString = line.split(";");
                    Country country = new Country(countriesString[0].trim(), countriesString[1].trim());
                    countriesMap.put(countriesString[1].trim(), country);
                }
            } while (line != null);
        }
    }

    @Bean
    Map<String, Country> getCountriesMap() {
        return Collections.unmodifiableMap(countriesMap);
    }

    @Bean("flight")
    Flight buildFlightFromCsv() throws IOException {
        Flight flight = new Flight("AA1234", 20);
        try(BufferedReader br = new BufferedReader(new FileReader("src/main/resources/flights_information.csv"))) {
            String line;
            do {
                line = br.readLine();
                if(line != null) {
                    String[] fields = line.split(";");
                    Passenger p = new Passenger(fields[0].trim());
                    p.setCountry(countriesMap.get(fields[1].trim()));
                    p.setIsRegistered(false);
                    flight.addPassenger(p);
                }
            } while (line != null);
        }
        return flight;
    }
}
```

注意：

- `L21` 行使用的 `Collections.unmodifiableMap()` 方法可以防止 `countriesMap` 被 `Spring` 容器注入后被人为修改；
- `countriesMap` 的初始化是通过 `FlightBuilder` 的构造函数触发的，相当于走了个捷径；
- 由于 `FlightBuilder` 从测试环境迁移到了 `src` 源码环境，实例化 `Flight` 的逻辑也要同步更新 `CSV` 数据源的访问路径。

接着改造 `Country` 实体类，添加两个 `JPA` 注解：

```java
@Entity
public class Country {
    private String name;

    @Id
    private String codeName;
    
    public Country() {
        this(null, null);
    }

    public Country(String name, String codeName) {
        this.name = name;
        this.codeName = codeName;
    }
    // -- snip --
}
```

注意：这里加注 `@Entity` 注解后，需要补一个无参构造函数，以便持久层在实例化 `Country` 时通过 **反射机制** 调用该构造函数 [^1]。

按照 `MVC` 的设计模式，`Country` 这个 `Model` 层准备就绪了，还需要 `C` 代表的 `Controller` 层，以及沟通 `M` 和 `C` 的数据访问层（`Data Access Object` 层，即`DAO` 层）。先创建 `DAO` 层 `CountryRepository`：

```java
public interface CountryRepository extends JpaRepository<Country, String> {
}
```

继承 `JpaRepository` 后，`CountryRepository` 接口就自带了一组基于 `Country` 实体类的 `CRUD` 操作方法，不必再手动实现一遍这些逻辑了；同时，继承该接口相当于让 `Spring` 在类路径 `CLASSPATH` 下扫描该接口，自动生成一个 `Bean` 实例，无需再添加注解 `@Repository` 了。

然后是 `Country` 实体对应的 `Controller` 层 `CountryController`：

```java
@RestController
public class CountryController {

    @Autowired
    private CountryRepository repository;

    @GetMapping("/countries")
    List<Country> findAll() {
        return repository.findAll();
    }
}
```

最后，还需要将 `countriesMap` 中的数据提前存入内存数据库，通过改造启动类 `Application` 实现：

```java
@SpringBootApplication
@Import(FlightBuilder.class)
public class Application {

    @Autowired
    private Map<String, Country> countriesMap;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner configureRepository(CountryRepository countryRepository) {
        return args -> countryRepository.saveAll(countriesMap.values());
    }

}
```

上述代码中，`configureRepository()` 方法将返回一个 `CommandLineRunner` 接口实例。`CommandLineRunner` 让开发者可以直接访问从命令行启动项目时指定的 `args` 参数，并且通过这个 `Spring` 注入的 `Bean` 实例还能在项目启动 **之前** 执行由 `L14` 返回的自定义逻辑——遍历 `countriesMap` 中的国家数据并存入数据库，以确保客户端调用 `GET /countries` 端点时有数据。

最后，运行 `Application` 的 `main` 方法启动项目：

![](../assets/18.3.png)

接口是否可用，有两种检测方法——

一是通过浏览器直接访问 [http://localhost:8081/countries](http://localhost:8081/countries)：

![](../assets/18.4.png)

二是通过命令行的 `curl` 命令访问：`curl -v localhost:8081/countries`：

![](../assets/18.5.png)

注意：`curl` 命令的 `-v` 参数表示展示详细信息。其中——

- 以 `*` 开头的行表示 **curl 信息**，显示连接建立过程；
- 以 `>` 开头的行表示 **请求信息**，显示客户端发送到服务器的内容；
- 以 `<` 开头的行表示 **响应头信息**，显示服务器返回的 `HTTP Header` 头内容；
- 最后一行为 **响应体**，即服务器返回的实际数据内容。



## 18.4 RESTful API 接口的测试

### 18.4.1 新增 Passenger REST API 接口

前面建立关于 `Country` 实体的 `REST API` 接口旨在跑通整个流程，而本章要重点演示的是基于乘客实体 `Passenger` 的 `REST API` 接口。它同样涉及 `CRUD` 基础操作。利用 `IDEA` 的 `Endpoints` 工具可以快速查看当前项目的所有 `API` 接口，工具窗口下方甚至还给出了每个接口的 `OpenAPI` 规范描述以及 `curl` 命令的调用格式，非常方便：

![](../assets/18.6.png)

为此，需要先改造 `Passenger` 实体类。根据定义，每个乘客实例都包含三个属性：`name`、`country` 和 `isRegistered`。其中 `country` 是 `Country` 类的实例，在考虑乘客数据的持久化时必须明确 `Country` 和 `Passenger` 之间的映射关系。这里显然是 **一对多** 关系：一则国籍信息可以被多个乘客实例引用。因此改造如下：

```java
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Entity
public class Passenger {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    private Country country;

    private boolean isRegistered;

    public Passenger() {
        this(null);
    }
    public Passenger(String name) {
        this.name = name;
    }
    // -- snip --
}
```

与 `Country` 不同，`Passenger` 没有现成的唯一标识做主键，于是新增一个 `id` 属性；此外，在 `country` 属性上添加了 `@ManyToOne` 注解。单击属性左侧的关系图标还可以查看 `IDEA` 提供的持久层视图，进一步验证两者的对应关系：

![](../assets/18.7.png)

接着创建乘客的 `DAO` 接口 `PassengerRepository`，注意主键类型必须与实体类保持一致（`Long`）：

```java
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
```

然后创建 `PassengerController`，分别实现 `CRUD` 各请求的响应逻辑：

```java
@RestController
public class PassengerController {

    @Autowired
    private PassengerRepository repository;

    @Autowired
    private Map<String, Country> countriesMap;

    @GetMapping("/passengers")
    List<Passenger> findAll() {
        return repository.findAll();
    }

    @PostMapping("/passengers")
    @ResponseStatus(HttpStatus.CREATED)
    Passenger createPassenger(@RequestBody Passenger passenger) {
        return repository.save(passenger);
    }

    @GetMapping("/passengers/{id}")
    Passenger findPassenger(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PassengerNotFoundException(id));
    }

    @PatchMapping("/passengers/{id}")
    Passenger patchPassenger(@RequestBody Map<String, String> updates, @PathVariable Long id) {

        return repository.findById(id)
                .map(passenger -> {

                    String name = updates.get("name");
                    if (null != name) {
                        passenger.setName(name);
                    }

                    Country country = countriesMap.get(updates.get("country"));
                    if (null != country) {
                        passenger.setCountry(country);
                    }

                    String isRegistered = updates.get("isRegistered");
                    if (null != isRegistered) {
                        passenger.setIsRegistered(isRegistered.equalsIgnoreCase("true"));
                    }
                    return repository.save(passenger);
                })
                .orElseThrow(() -> new PassengerNotFoundException(id));

    }

    @DeleteMapping("/passengers/{id}")
    void deletePassenger(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
```

上述代码除了 `@PatchMapping` 用得较少外，其余都比较常见；部分项目可能处于安全考虑还会拦截除 `GET`、`POST` 以外的其他请求类型。这里只演示标准模式下的接口写法。对于需要手动设置响应码的，可像 `@ResponseStatus(HttpStatus.CREATED)` 这样添加到对应的处理方法上。

为确保项目启动时获取到有效的乘客列表，还需要同步修改启动类 `Application`，将包含乘客信息的 `CSV` 文件内容提前存入内存数据库（`L20`）：

```java
@SpringBootApplication
@Import(FlightBuilder.class)
public class Application {

	@Autowired
	private Map<String, Country> countriesMap;

	@Autowired
	private Flight flight;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

    @Bean
    CommandLineRunner configureRepository(CountryRepository countryRepository,
                                          PassengerRepository passengerRepository) {
		return args -> {
			countryRepository.saveAll(countriesMap.values());
			passengerRepository.saveAll(flight.getPassengers());
		};
	}
}
```



### 18.4.2 简单测试乘客 API

启动项目，然后使用 `curl` 命令尝试调用新创建的乘客信息 `REST API` 接口（浏览器无法直接发送 `POST` 请求，因此不考虑）。这里顺便梳理一下在 `Windows Terminal` 终端的 `PowerShell` 环境下运行 `curl` 的几个注意事项。

先尝试获取所有乘客信息：

```powershell
curl -v localhost:8081/passengers
```

实测结果（成功）：

![](../assets/18.8.png)

再查用 `id` 查看单个乘客信息：

```powershell
curl -v localhost:8081/passengers/4
```

实测结果：

![](../assets/18.9.png)

接着尝试修改该乘客信息，调用端点 `PATCH /passengers/{id}`：

```powershell
curl -v -X PATCH localhost:8081/passengers/4 -H "Content-type:application/json" -d '{"name":"Sophia Jones", "country":"AU", "isRegistered":"true"}'
```

运行结果：

![](../assets/18.10.png)

接着删除该乘客：

```powershell
curl -v -X DELETE localhost:8081/passengers/4
```

运行结果：

![](../assets/18.11.png)

最后再新增一条乘客记录，使用 `POST /passengers` 端点：

```powershell
curl -v -X POST localhost:8081/passengers -H "Content-type:application/json" -d '{"name":"John Smith"}'
```

![](../assets/18.12.png)

> [!tip]
>
> **实用技巧：在 PowerShell 中格式化 curl 命令**
>
> 上述 `curl` 命令虽然和书中效果相同，但都是 **单行命令**，无法像原书那样对较长命令进行换行或手动缩进：
>
> ![](../assets/18.13.png)
>
> 经实测，`PowerShell` 中的换行主要有以下几种情况：
>
> |         场景         |                     方法                      | 示例                     |
> | :------------------: | :-------------------------------------------: | :----------------------- |
> |    **管道符** 后     |     紧跟管道符，按 <kbd>Enter</kbd> 换行      | ![](../assets/18.16.png) |
> |   **英文逗号** 后    |      紧跟逗号，按 <kbd>Enter</kbd> 换行       | ![](../assets/18.15.png) |
> | **大括号 / 括号** 后 |   紧跟 **左括号**，按 <kbd>Enter</kbd> 换行   | ![](../assets/18.17.png) |
> |  **点号运算符** 后   |    紧跟点运算符，按 <kbd>Enter</kbd> 换行     | ![](../assets/18.14.png) |
> |     **其他位置**     | 用 **反引号** <kbd>`</kbd> + <kbd>Enter</kbd> | ![](../assets/18.18.png) |
>
> 因此，格式化后的 `PATCH` 请求可以写作：
>
> ```powershell
> curl -v -X PATCH localhost:8081/passengers/4 `
>     -H "Content-Type: application/json"`
>     -d '{
>           "name": "Sophia Jones",
>           "country": "AU",
>           "isRegistered": "true"
>        }'
> ```
>
> 实测效果（红框部分为触发换行的地方）：
>
> ![](../assets/18.19.png)
>
> 最后，**混合使用单引号和双引号** 可以避免手动输入 `JSON` 内的大量转移符 `\`，也算一个提速小技巧吧。



### 18.4.3 REST API 接口的测试

到这里才是全章的重点内容，不过作者是对照大段测试用例逐一解释的，很多地方并没有展开讲，有点小遗憾；我准备按不同的接口进行梳理，并结合实测情况补充必要内容。

#### 1 GET 请求测试：查数据列表

先来看 `GET /countries` 的测试。创建测试类 `RestApplicationTest`：

```java
@SpringBootTest
@AutoConfigureMockMvc
@Import(FlightBuilder.class)
public class RestApplicationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private Map<String, Country> countriesMap;

    @MockitoBean
    private CountryRepository countryRepository;

    @Test
    void testGetAllCountries() throws Exception {
        when(countryRepository.findAll()).thenReturn(new ArrayList<>(countriesMap.values()));
        mvc.perform(get("/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)));

        verify(countryRepository, times(1)).findAll();
    }
}
```

可以看到，`REST API` 接口的测试总共分三步：

- 利用依赖注入初始化相关参数；
- 设置期望的状态；
- 模拟执行并断言结果；

其中，`MockMvc` 型依赖（`mvc`）是测试逻辑的主入口，其相关对象的自动配置是通过 `@AutoConfigureMockMvc` 注解启用的，并通过 `@MockitoBean` 注解具体注入（`L7`）。

对于 `GET /countries` 端点，我们要验证的是：

1. 状态码是否为 `200`；
2. `Header` 里的 `Content-Type` 是否是 `JSON` 格式的；
3. 总记录数是否正确。

因此，需要先用 `when/thenReturn` 设置大的流程，再用 `mvc` 对象模拟调用一次端点，并用 `MockMvc` 常用的链式写法断言上述三个指标，最后调用 `verifiy()` 方法严格限定调用次数，正式启动模拟逻辑即可。

这里比较有意思的是断言总记录数的相关写法：`jsonPath("$", hasSize(3))`。其中，第一个参数 `"$"` 表示要遍历的根节点（The root element to query）。这是一门用来读取 `JSON` 内容的 `Java` 专用 `DSL` 语言，全称叫 `Jayway JsonPath`，具体用法详见 [GitHub 官方文档](https://github.com/json-path/JsonPath)。

本地实测结果：

![](../assets/18.20.png)

同理可测 `GET /passengers` 端点：

```java
@Autowired
private Flight flight;

@MockitoBean
private PassengerRepository passengerRepository;

@Test
void testGetAllPassengers() throws Exception {
    when(passengerRepository.findAll()).thenReturn(new ArrayList<>(flight.getPassengers()));

    mvc.perform(get("/passengers"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(20)));

    verify(passengerRepository, times(1)).findAll();
}
```

可以看到，测试逻辑都是声明式的写法，无需死记硬背，真正要用的时候多写几遍就够了。

实测截图：

![](../assets/18.21.png)



#### 2 GET 请求测试：查单个记录

由于 `spring-boot-starter-data-jpa` 中查询单个记录用的是 `repository.findById(id).orElseThrow(...)`，因此除了考虑正常流程还得测一下查不到的异常情况。这里作者省略了太多内容，没有详细展开；本着实战精神，我再补充点内容。

正常情况下，查单条记录应该写成：

```java
@Test
void testFindPassengerById() throws Exception {
    Passenger passenger = new Passenger("John Smith");
    Country country = countriesMap.get("UK");
    passenger.setCountry(country);
    passenger.setIsRegistered(false);
    when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
    
    mvc.perform(get("/passengers/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name", is("John Smith")))
            .andExpect(jsonPath("$.registered", is(Boolean.FALSE)))
            .andExpect(jsonPath("$.country.name", is("United Kingdom")))
            .andExpect(jsonPath("$.country.codeName", is("UK")));
    
    verify(passengerRepository, times(1)).findById(1L);
}
```

运行肯定也是没问题的：

![](../assets/18.23.png)

但可能是后面还有个 `PATCH /passenger/{id}` 端口，里面也会调用 `findById()` 方法，因此上述正常情况就跳过了，直接补了一个抛异常的情况：

```java
@Test
void testPassengerNotFound() {
    Throwable throwable = assertThrows(NestedServletException.class, () -> 
            mvc.perform(get("/passengers/30")).andExpect(status().isNotFound()));
    assertEquals(PassengerNotFoundException.class, throwable.getCause().getClass());
}

// 自定义异常：
public class PassengerNotFoundException extends RuntimeException {
    public PassengerNotFoundException(Long id) {
        super("Passenger id not found : " + id);
    }
}
```

上述用例写得很简洁，只是有点 **过于简洁** 了。按照刚才的套路，应该写成这样才对啊：

```java
@Test
void testPassengerNotFound() {
    Throwable throwable = assertThrows(ServletException.class, () -> {
        when(passengerRepository.findById(30L)).thenThrow(PassengerNotFoundException.class);

        mvc.perform(get("/passengers/30"))
                .andExpect(status().isNotFound());

        verify(passengerRepository, times(1)).findById(30L);
    });
    assertEquals(PassengerNotFoundException.class, throwable.getCause().getClass());
}
```

为了一探究竟，我用调试模式跟踪了一下，结果发现测试逻辑执行到 `L6` 就中断返回了：

![](../assets/18.22.png)

我这才悟了：注定要抛的异常何必画蛇添足写个 `when()`？既然抛异常了何必再 `verify()` 手动验证一次执行？于是，就有了上面的简化版，一切都说得通了。



#### 3 POST 请求测试

有了前面两组用例热身，发送 `POST` 请求的用例简直不要太简单，标准的 `AAA` 模式（`Arrange-Act-Assert`，即准备-执行-验证）：

```java
@Test
void testPostPassenger() throws Exception {
    Passenger passenger = new Passenger("Peter Michelsen");
    passenger.setCountry(countriesMap.get("US"));
    passenger.setIsRegistered(false);
    when(passengerRepository.save(passenger)).thenReturn(passenger);

    mvc.perform(post("/passengers")
            .content(new ObjectMapper().writeValueAsString(passenger))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name", is("Peter Michelsen")))
            .andExpect(jsonPath("$.country.codeName", is("US")))
            .andExpect(jsonPath("$.country.name", is("USA")))
            .andExpect(jsonPath("$.registered", is(Boolean.FALSE)));

    verify(passengerRepository, times(1)).save(passenger);
}
```

其实还是有不一样的地方，比如传入 `perform()` 方法的参数：

```java
MockHttpServletRequestBuilder mockRequest = post("/passengers")
    .content(new ObjectMapper().writeValueAsString(passenger))
    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
```

其中第 `L2` 行是利用了第三方库 `Jackson` 的标准接口将 `passenger` 转成了 `JSON` 字符串；而第三行的写法可以避免手动输错 `Header` 请求头。

另外，示例代码并没有单独声明一个 `MockHttpServletRequestBuilder` 对象，而是直接放到了 `perform()` 中，这也是尽量使用声明式编程的考虑。

至于状态码的断言，严格意义上的基于 `RESTful` 风格的 `POST` 请求其实不是 `200` 而是 `201`（`status().isCreated()`），该规范的时候还是不要遗漏这些细节。

本地运行结果：

![](../assets/18.24.png)



#### 4 PATCH 请求测试

根据 `REST API` 的说法，`PATCH` 请求就是打补丁用的，需要修改哪些字段就提交哪些，传统模式下生成的更新语句也是动态的才对，因此演示代码和测试用例都写得比其他端点多一些：

```java
@Test
void testPatchPassenger() throws Exception {
    Passenger passenger = new Passenger("Sophia Graham");
    passenger.setCountry(countriesMap.get("UK"));
    passenger.setIsRegistered(false);
    when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
    when(passengerRepository.save(passenger)).thenReturn(passenger);
    String updates = "{\"name\":\"Sophia Jones\", \"country\":\"AU\", \"isRegistered\":\"true\"}";

    mvc.perform(patch("/passengers/1")
                    .content(updates)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            ).andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

    verify(passengerRepository, times(1)).findById(1L);
    verify(passengerRepository, times(1)).save(passenger);
}
```

上述逻辑和 `POST` 类似，请求的 `BODY` 正文部分都需要用 `content()` 方法传 `JSON` 字符串。整个测试流程也是标准的 `AAA` 模式（强烈建议手敲一遍加深印象）。最终实测结果：

![](../assets/18.25.png)



#### 5 DELETE 请求测试

最后的 `DELETE /passengers/{id}` 端点反而最简单，因为遵循幂等设计，不必单独考虑删除了不存在的乘客 `id` 的情况：

```java
@Test
public void testDeletePassenger() throws Exception {
    mvc.perform(delete("/passengers/4"))
            .andExpect(status().isOk());

    verify(passengerRepository, times(1)).deleteById(4L);
}
```

这里用 `curl` 验证删除一个不存在的乘客更有说服力：

![](../assets/18.26.png)

最后，如果实在不放心，还可以在 `IDEA` 中导出测试报表查看乘客 `Controller` 中的测试用例的覆盖情况：

![](../assets/18.27.png)



---

[^1]: 如果没有显式定义带参构造函数（本例显然不满足），编译器会默认创建一个无参构造函数。




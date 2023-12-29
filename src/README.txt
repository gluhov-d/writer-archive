Необходимо реализовать консольное CRUD приложение, которое взаимодействует с БД и позволяет выполнять все CRUD операции над сущностями:
Writer (id, firstName, lastName, List<Post> posts)
Post (id, content, created, updated, List<Label> labels)
Label (id, name)
PostStatus (enum ACTIVE, UNDER_REVIEW, DELETED)
Требования:
Придерживаться шаблона MVC (пакеты model, repository, service, controller, view)
Для миграции БД использовать https://www.liquibase.org/
Сервисный слой приложения должен быть покрыт юнит тестами (junit + mockito).
Слои:
model - POJO клаcсы
repository - классы, реализующие доступ к БД
controller - обработка запросов от пользователя
view - все данные, необходимые для работы с консолью



Например: Developer, DeveloperRepository, DeveloperController, DeveloperView и т.д.


Для репозиторного слоя желательно использовать базовый интерфейс:
interface GenericRepository<T,ID>

interface DeveloperRepository extends GenericRepository<Developer, Long>

class JdbcDeveloperRepositoryImpl implements DeveloperRepository

Для импорта библиотек использовать Maven
Результатом выполнения проекта должен быть отдельный репозиторий на github, с описанием задачи, проекта и инструкцией по локальному запуску проекта.

Технологии: Java, MySQL, JDBC, Maven, Liquibase, JUnit, Mockito
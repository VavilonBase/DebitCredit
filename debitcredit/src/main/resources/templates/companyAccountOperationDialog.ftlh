<#import "parts/common.ftlh" as c>

<@c.page "Удаление операции">
    <h2>Вы действительно хотите удалить операцию:</h2>
    <table class="table">
        <thead>
        <tr>
            <th scope="col">#</th>
            <th scope="col">Дата</th>
            <th scope="col">Документ</th>
            <th scope="col">Дебет</th>
            <th scope="col">Кредит</th>
            <th scope="col">Итого</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <th scope="row">1</th>
            <td>${operation.getDocumentDateString()}</td>
            <td>${operation.getDocumentName()}</td>
            <#if operation.isDebit>
                <td>${operation.accountOperation}</td>
                <td></td>
            <#else>
                <td></td>
                <td>${operation.accountOperation}</td>
            </#if>
            <td>${operation.currentBalance}</td>
        </tr>
        </tbody>
    </table>

    <a class="btn btn-danger" href="/company/${company.id}/${operation.id}/delete?OK=true">Удалить</a>
    <a class="btn btn-primary" href="/company/${company.id}">Отменить</a>
</@c.page>
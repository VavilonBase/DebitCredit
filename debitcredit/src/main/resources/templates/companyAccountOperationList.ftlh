<#import "parts/common.ftlh" as c>

<@c.page "Операции по компании">
    <h2>${company.name}</h2>
    <div class="container">
        <div class="row">
            <#include "parts/operationEdit.ftlh"/>
        </div>
        <div class="row">
            <#include "parts/filterOperation.ftlh"/>
        </div>
        <div class="row">
            <form action="/company/${company.id}/download" method="GET">
                <input type="hidden" name="dateStart"
                       value="<#if intervalDate?? && intervalDate.dateStart??>${intervalDate.getDateStartString()}</#if>"/>
                <input type="hidden" name="dateEnd"
                       value="<#if intervalDate?? && intervalDate.dateEnd??>${intervalDate.getDateEndString()}</#if>"/>
                <div class="form-group mt-2">
                    <button class="btn btn-primary" type="submit">Загрузить Акт Сверки</button>
                </div>
            </form>
        </div>
    </div>

    <table class="table">
        <thead>
        <tr>
            <th scope="col">#</th>
            <th scope="col">Дата</th>
            <th scope="col">Документ</th>
            <th scope="col">Дебет</th>
            <th scope="col">Кредит</th>
            <th scope="col">Итого</th>
            <th scope="col"></th>
        </tr>
        </thead>
        <tbody>
        <#list operationSet as operation>
            <tr>
                <th scope="row">${operation?counter}</th>
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
                <td><a class="btn btn-danger" href="/company/${company.id}/${operation.id}/delete">Удалить</a></td>
            </tr>
        </#list>
        </tbody>
    </table>

</@c.page>
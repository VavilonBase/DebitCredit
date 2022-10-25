<#import "parts/common.ftlh" as c>

<@c.page "Операции по компании">
    <h2>${company.name}</h2>
    <#include "parts/operationEdit.ftlh"/>
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
            </tr>
        </#list>
        </tbody>
    </table>

</@c.page>
<#import "parts/common.ftlh" as c>

<@c.page "Список компаний">
    <h3>Список компаний</h3>
    <#include "parts/companyEdit.ftlh"/>
    <table class="table">
        <thead>
        <tr>
            <th scope="col">#</th>
            <th scope="col">Название</th>
            <th scope="col"></th>
        </tr>
        </thead>
        <tbody>
        <#list companyList as company>
            <tr>
                <th scope="row">${company?counter}</th>
                <td>${company.name}</td>
                <td><a class="btn btn-info" href="company/${company.id}">Операции</a>
                    <a class="btn btn-primary" href="company?company=${company.id}">Редактировать</a>
                    <a class="btn btn-danger" href="#">Удалить</a></td>
            </tr>
        </#list>
        </tbody>
    </table>

</@c.page>
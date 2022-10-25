<#import "parts/common.ftlh" as c>

<@c.page "Список документов">
    <h3>Список документов</h3>
    <#include "parts/documentEdit.ftlh">
    <table class="table">
        <thead>
        <tr>
            <th scope="col">#</th>
            <th scope="col">Название</th>
            <th scope="col"></th>
        </tr>
        </thead>
        <tbody>
        <#list documentList as document>
            <tr>
                <th scope="row">${document?counter}</th>
                <td>${document.documentName}</td>
                <td><a href="/document?document=${document.id}">Редактировать</a>
                    <a href="#">Удалить</a></td>
            </tr>
        </#list>
        </tbody>
    </table>

</@c.page>
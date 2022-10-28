<a class="btn btn-primary mt-3" data-bs-toggle="collapse" href="#companyAccountOperationForm" role="button" aria-expanded="false" aria-controls="collapseExample">
    Добавление операции
</a>
<div class="collapse <#if operation??>show</#if>" id="companyAccountOperationForm">
    <div class="form-group mt-3">
        <form method="POST">
            <div class="form-group mt-2">
                <select class="form-select ${(documentError??)?string('is-invalid', '')}" name="document" aria-label="Default select example">
                    <option selected>Выберите документ</option>
                    <#if operation?? && operation.document??>
                        <#list documentList as document>
                            <#if operation.document.id == document.id>
                                <option selected value="${document.id}">${document.documentName}</option>
                            <#else>
                                <option value="${document.id}">${document.documentName}</option>
                            </#if>
                        </#list>
                    <#else>
                        <#list documentList as document>
                            <option value="${document.id}">${document.documentName}</option>
                        </#list>
                    </#if>
                </select>
                <#if documentError??>
                    <div class="invalid-feedback">
                        ${documentError}
                    </div>
                </#if>
            </div>
            <div class="form-group mt-2">
                <input type="text" name="documentDate" class="form-control ${(documentDateError??)?string('is-invalid', '')}"
                       value="<#if operation?? && operation.documentDate??>${operation.getDocumentDateString()}</#if>" placeholder="Введите дату (ДД.ММ.ГГГГ)" />
                <#if documentDateError??>
                    <div class="invalid-feedback">
                        ${documentDateError}
                    </div>
                </#if>
            </div>

            <div class="form-group mt-2">
                <input type="text" name="comment" class="form-control ${(commentError??)?string('is-invalid', '')}"
                       value="<#if operation?? && operation.comment??>${operation.comment}</#if>" placeholder="Введите комментарий (необязательно)" />
                <#if commentError??>
                    <div class="invalid-feedback">
                        ${commentError}
                    </div>
                </#if>
            </div>

            <div class="form-group mt-2">
                <input type="text" name="documentNumber" class="form-control ${(documentNumberError??)?string('is-invalid', '')}"
                       value="<#if operation?? && operation.documentNumber??>${operation.documentNumber}</#if>" placeholder="Введите номер документа (необязательно)" />
                <#if documentNumberError??>
                    <div class="invalid-feedback">
                        ${documentNumberError}
                    </div>
                </#if>
            </div>



            <div class="form-group mt-2">
                <div class="form-check">
                    <input class="form-check-input" type="radio" value="true" name="isDebit" id="flexRadioDefault1" checked>
                    <label class="form-check-label" for="flexRadioDefault1">
                        Дебет
                    </label>
                </div>

                <div class="form-check">
                    <input class="form-check-input" type="radio" value="false" name="isDebit" id="flexRadioDefault2">
                    <label class="form-check-label" for="flexRadioDefault2">
                        Кредит
                    </label>
                </div>


            </div>

            <div class="form-group mt-2">
                <input type="text" name="accountOperation" class="form-control ${(accountOperationError??)?string('is-invalid', '')}"
                       value="<#if operation?? && operation.accountOperation??>${operation.getEditAccountOperation()}</#if>" placeholder="Введите сумму списания (начисления)" />
                <#if accountOperationError??>
                    <div class="invalid-feedback">
                        ${accountOperationError}
                    </div>
                </#if>
            </div>

            <div class="form-group mt-2">
                <#if operation??>
                    <button type="submit" class="btn btn-primary">Изменить операцию</button>
                <#else>
                    <button type="submit" class="btn btn-primary">Добавить операцию</button>
                </#if>
            </div>
        </form>
        <#if operation??>
            <a class="btn btn-danger" href="/company/${company.id}">Отменить изменение</a>
        </#if>
    </div>
</div>
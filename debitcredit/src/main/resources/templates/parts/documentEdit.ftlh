<a class="btn btn-primary mt-3" data-bs-toggle="collapse" href="#collapseAddMessageForm" role="button" aria-expanded="false" aria-controls="collapseExample">
    <#if document??>
        Изменение документа
    <#else>
        Добавление документа
    </#if>
</a>
<div class="collapse <#if document??>show</#if>" id="collapseAddMessageForm">
    <div class="form-group mt-3">
        <form method="POST">
            <div class="form-group mt-2">
                <input type="text" name="documentName" class="form-control ${(documentNameError??)?string('is-invalid', '')}"
                       value="<#if document?? && document.documentName??>${document.documentName}</#if>"
                       placeholder="Введите название документа" />
                <#if documentNameError??>
                    <div class="invalid-feedback">
                        ${documentNameError}
                    </div>
                </#if>
            </div>

            <div class="form-group mt-2">
                <button type="submit" class="btn btn-primary">
                    <#if document??>
                        Изменить документ
                    <#else>
                        Добавить документ
                    </#if>
                </button>
            </div>
        </form>
    </div>
</div>
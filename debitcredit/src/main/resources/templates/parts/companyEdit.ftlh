<a class="btn btn-primary mt-3" data-bs-toggle="collapse" href="#collapseAddMessageForm" role="button" aria-expanded="false" aria-controls="collapseExample">
    <#if company??>
        Изменение компании
    <#else>
        Добавление компании
    </#if>
</a>
<div class="collapse <#if company??>show</#if>" id="collapseAddMessageForm">
    <div class="form-group mt-3">
        <form method="POST">
            <div class="form-group mt-2">
                <input type="text" name="name" class="form-control ${(nameError??)?string('is-invalid', '')}"
                       value="<#if company?? && company.name??>${company.name}</#if>" placeholder="Введите имя компании" />
                <#if nameError??>
                    <div class="invalid-feedback">
                        ${nameError}
                    </div>
                </#if>
            </div>

            <div class="form-group mt-2">
                <input type="text" name="inn" class="form-control ${(innError??)?string('is-invalid', '')}"
                       value="<#if company?? && company.inn??>${company.inn}</#if>" placeholder="Введите ИНН компании" />
                <#if innError??>
                    <div class="invalid-feedback">
                        ${innError}
                    </div>
                </#if>
            </div>

            <div class="form-group mt-2">
                <button type="submit" class="btn btn-primary">
                    <#if company??>
                        Изменить компанию
                    <#else>
                        Добавить компанию
                    </#if>
                </button>
            </div>
        </form>
    </div>
</div>
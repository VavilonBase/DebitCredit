<a class="btn btn-primary mt-3" data-bs-toggle="collapse" href="#filterForm" role="button" aria-expanded="false" aria-controls="collapseExample">
    Фильтр
</a>
<div class="collapse <#if intervalDate.dateStart?? || intervalDate.dateEnd??>show</#if>" id="filterForm">
    <div class="form-group mt-3">
        <form method="GET">
            <div class="form-group mt-2">
                <input type="text" name="dateStart" class="form-control ${(dateStartError??)?string('is-invalid', '')}"
                       value="<#if intervalDate?? && intervalDate.dateStart??>${intervalDate.getDateStartString()}</#if>"
                       placeholder="Введите дату начала(ДД.ММ.ГГГГ)" />
                <#if dateStartError??>
                    <div class="invalid-feedback">
                        ${dateStartError}
                    </div>
                </#if>
            </div>
            <div class="form-group mt-2">
                <input type="text" name="dateEnd" class="form-control ${(dateEndError??)?string('is-invalid', '')}"
                       value="<#if intervalDate?? && intervalDate.dateEnd??>${intervalDate.getDateEndString()}</#if>"
                       placeholder="Введите дату окончания(ДД.ММ.ГГГГ)" />
                <#if dateEndError??>
                    <div class="invalid-feedback">
                        ${dateEndError}
                    </div>
                </#if>
            </div>


            <div class="form-group mt-2">
                <button type="submit" class="btn btn-primary">Применить фильтр</button>
            </div>
        </form>
    </div>
</div>
package domain.usecase.workbook

import data.Constants
import domain.model.dto.TestDTO
import domain.repository.WorkbookRepository
import org.apache.poi.xssf.usermodel.XSSFWorkbookType

class WriteDataToWorkbookUseCase(
    private val workbookRepository: WorkbookRepository
) {
    suspend fun execute(
        testDTO: TestDTO,
        folderPath: String,
        dataFormats: Map<String, Boolean>,
    ) {

        val formats = mutableListOf<XSSFWorkbookType>()
        val extensions = mutableListOf<String>()

        dataFormats.forEach { (t, u) ->
            if (u) {
                val item = Constants.Settings.FORMAT_LIST[t] ?: XSSFWorkbookType.XLSX
                formats.add(item)
                extensions.add(t)
            }
        }

        workbookRepository.writeDataInWorkbook(
            testDTO = testDTO,
            folderPath = folderPath,
            formats = formats.toList(),
            extensions = extensions,
        )
    }
}
package com.hnkjzy.ecg_collection.service.impl.analysis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnkjzy.ecg_collection.common.exception.BusinessException;
import com.hnkjzy.ecg_collection.common.result.ResultCode;
import com.hnkjzy.ecg_collection.mapper.analysis.DiagnosisReportManagerMapper;
import com.hnkjzy.ecg_collection.model.dto.analysis.DiagnosisReportPageQueryDto;
import com.hnkjzy.ecg_collection.model.vo.analysis.DiagnosisReportDetailVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.DiagnosisReportPageItemVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.DiagnosisReportPageResultVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.DiagnosisReportPdfFileVo;
import com.hnkjzy.ecg_collection.model.vo.analysis.DiagnosisReportStatusDictVo;
import com.hnkjzy.ecg_collection.model.vo.common.DictOptionVo;
import com.hnkjzy.ecg_collection.service.analysis.DiagnosisReportManagerService;
import com.hnkjzy.ecg_collection.service.impl.BaseServiceImpl;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Diagnosis report management service implementation.
 */
@Service
@RequiredArgsConstructor
public class DiagnosisReportManagerServiceImpl extends BaseServiceImpl implements DiagnosisReportManagerService {

    private static final long DEFAULT_PAGE_NUM = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 200L;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DiagnosisReportManagerMapper diagnosisReportManagerMapper;

    @Override
    public DiagnosisReportPageResultVo pageReports(DiagnosisReportPageQueryDto queryDto) {
        DiagnosisReportPageQueryDto query = normalizePageQuery(queryDto);
        Integer statusCode = parseStatusCode(query.getStatus(), false);

        long pageNum = normalizePageNum(query.getPageNum());
        long pageSize = normalizePageSize(query.getPageSize());

        Page<DiagnosisReportPageItemVo> page = new Page<>(pageNum, pageSize);
        IPage<DiagnosisReportPageItemVo> pageData = diagnosisReportManagerMapper.selectReportPage(page, query, statusCode);

        List<DiagnosisReportPageItemVo> records = pageData.getRecords();
        if (records == null) {
            records = Collections.emptyList();
        }

        return DiagnosisReportPageResultVo.builder()
                .total(pageData.getTotal())
                .pages(pageData.getPages())
                .list(records)
                .build();
    }

    @Override
    public DiagnosisReportStatusDictVo getStatusDicts() {
        DiagnosisReportStatusDictVo dictVo = new DiagnosisReportStatusDictVo();

        List<DictOptionVo> statusList = new ArrayList<>();
        statusList.add(buildOption("", "全部状态"));
        statusList.add(buildOption("待生成", "待生成"));
        statusList.add(buildOption("已审核", "已审核"));

        dictVo.setStatusList(statusList);
        return dictVo;
    }

    @Override
    public DiagnosisReportDetailVo getReportDetail(Long reportId) {
        if (reportId == null || reportId <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "reportId 参数不合法");
        }

        DiagnosisReportDetailVo detailVo = diagnosisReportManagerMapper.selectReportDetail(reportId);
        if (detailVo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "诊断报告不存在");
        }
        if (!StringUtils.hasText(detailVo.getStatus())) {
            detailVo.setStatus(reportStatusToText(detailVo.getReportStatus()));
        }
        return detailVo;
    }

    @Override
    public DiagnosisReportPdfFileVo downloadReportPdf(Long reportId) {
        DiagnosisReportDetailVo detailVo = getReportDetail(reportId);
        byte[] content = buildPdf(detailVo);
        String fileName = buildFileName(detailVo);
        return new DiagnosisReportPdfFileVo(fileName, content);
    }

    private DiagnosisReportPageQueryDto normalizePageQuery(DiagnosisReportPageQueryDto queryDto) {
        DiagnosisReportPageQueryDto query = queryDto == null ? new DiagnosisReportPageQueryDto() : queryDto;
        query.setReportNo(trimToNull(query.getReportNo()));
        query.setPatientName(trimToNull(query.getPatientName()));
        query.setStatus(trimToNull(query.getStatus()));
        return query;
    }

    private Integer parseStatusCode(String status, boolean required) {
        if (!StringUtils.hasText(status)) {
            if (required) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "status 参数不合法");
            }
            return null;
        }

        String value = status.trim();
        if ("0".equals(value) || "1".equals(value) || "待生成".equals(value) || "pending".equalsIgnoreCase(value)) {
            return 1;
        }
        if ("2".equals(value) || "已审核".equals(value) || "audited".equalsIgnoreCase(value)) {
            return 2;
        }
        throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "status 参数不合法");
    }

    private long normalizePageNum(Long pageNum) {
        if (pageNum == null || pageNum < 1) {
            return DEFAULT_PAGE_NUM;
        }
        return pageNum;
    }

    private long normalizePageSize(Long pageSize) {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        if (pageSize > MAX_PAGE_SIZE) {
            return MAX_PAGE_SIZE;
        }
        return pageSize;
    }

    private DictOptionVo buildOption(String value, String label) {
        return DictOptionVo.builder().value(value).label(label).build();
    }

    private String reportStatusToText(Integer reportStatus) {
        if (reportStatus == null || reportStatus == 0 || reportStatus == 1) {
            return "待生成";
        }
        if (reportStatus == 2 || reportStatus == 3 || reportStatus == 4) {
            return "已审核";
        }
        return "待生成";
    }

    private byte[] buildPdf(DiagnosisReportDetailVo detailVo) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 48, 48, 42, 42);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font sectionFont = new Font(baseFont, 12, Font.BOLD);
            Font textFont = new Font(baseFont, 11, Font.NORMAL);
            Font labelFont = new Font(baseFont, 11, Font.BOLD);

            Paragraph title = new Paragraph("心电诊断报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(16f);
            document.add(title);

            document.add(buildBaseInfoTable(detailVo, textFont, labelFont));
            document.add(buildSection("AI 原始诊断结论", safeText(detailVo.getAiConclusion()), sectionFont, textFont));
            document.add(buildSection("医生终审结论", safeText(detailVo.getDoctorConclusion()), sectionFont, textFont));
            document.add(buildSection("医生建议", safeText(detailVo.getDoctorSuggestion()), sectionFont, textFont));
            document.add(buildSection("审核意见", safeText(detailVo.getAuditOpinion()), sectionFont, textFont));

            document.close();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR.getCode(), "报告 PDF 生成失败");
        }
    }

    private PdfPTable buildBaseInfoTable(DiagnosisReportDetailVo detailVo, Font textFont, Font labelFont) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingAfter(12f);
        table.setWidths(new float[]{1.5f, 2.4f, 1.5f, 2.4f});

        addCellPair(table, "报告编号", safeText(detailVo.getReportNo()), textFont, labelFont);
        addCellPair(table, "报告状态", safeText(detailVo.getStatus()), textFont, labelFont);
        addCellPair(table, "患者姓名", safeText(detailVo.getPatientName()), textFont, labelFont);
        addCellPair(table, "住院号", safeText(detailVo.getHospitalNo()), textFont, labelFont);
        addCellPair(table, "性别", safeText(detailVo.getGender()), textFont, labelFont);
        addCellPair(table, "年龄", safeNumber(detailVo.getAge()), textFont, labelFont);
        addCellPair(table, "采集时间", formatDateTime(detailVo.getCollectionTime()), textFont, labelFont);
        addCellPair(table, "报告生成时间", formatDateTime(detailVo.getReportCreateTime()), textFont, labelFont);
        addCellPair(table, "报告医生", safeText(detailVo.getReportCreateDoctorName()), textFont, labelFont);
        addCellPair(table, "审核医生", safeText(detailVo.getAuditDoctorName()), textFont, labelFont);
        addCellPair(table, "审核时间", formatDateTime(detailVo.getAuditTime()), textFont, labelFont);
        addCellPair(table, "审核完整时间", formatDateTime(detailVo.getAuditTime()), textFont, labelFont);

        return table;
    }

    private Paragraph buildSection(String title, String content, Font sectionFont, Font textFont) {
        Paragraph section = new Paragraph();
        section.setSpacingBefore(6f);
        section.setSpacingAfter(10f);

        Paragraph titleParagraph = new Paragraph(title, sectionFont);
        titleParagraph.setSpacingAfter(4f);
        section.add(titleParagraph);

        Paragraph contentParagraph = new Paragraph(content, textFont);
        contentParagraph.setLeading(18f);
        section.add(contentParagraph);
        return section;
    }

    private void addCellPair(PdfPTable table, String label, String value, Font textFont, Font labelFont) {
        table.addCell(buildCell(label, labelFont, true));
        table.addCell(buildCell(value, textFont, false));
    }

    private PdfPCell buildCell(String text, Font font, boolean labelCell) {
        PdfPCell cell = new PdfPCell(new Paragraph(safeText(text), font));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(6f);
        cell.setBorderWidth(0.8f);
        cell.setBorderColor(Color.LIGHT_GRAY);
        if (labelCell) {
            cell.setBackgroundColor(new Color(244, 247, 250));
        }
        return cell;
    }

    private String safeText(String value) {
        if (!StringUtils.hasText(value)) {
            return "-";
        }
        return value.trim();
    }

    private String safeNumber(Number value) {
        if (value == null) {
            return "-";
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).stripTrailingZeros().toPlainString();
        }
        return String.valueOf(value);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    private String buildFileName(DiagnosisReportDetailVo detailVo) {
        String reportNo = trimToNull(detailVo.getReportNo());
        if (reportNo == null) {
            reportNo = "diagnosis-report-" + detailVo.getReportId();
        }
        return reportNo + ".pdf";
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}

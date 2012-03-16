package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.MavenReportException;

import fitnesse.responders.run.TestSummary;

public class MafiaReportGenerator {

    private static final String PLUGIN_RESOURCES = "nl/sijpesteijn/testing/fitnesse/plugins/resources";
    private final Sink sink;
    private final ResourceBundle bundle;
    private final File outputDirectory;
    private final List<MafiaTestResult> mafiaTestResults;

    public MafiaReportGenerator(final Sink sink, final ResourceBundle bundle,
            final ReporterPluginConfig reporterPluginConfig, final List<MafiaTestResult> mafiaTestResults) {
        this.sink = sink;
        this.bundle = bundle;
        this.outputDirectory = new File(reporterPluginConfig.getFitnesseTestResultsDirectory());
        this.mafiaTestResults = mafiaTestResults;
    }

    public void generate() throws MavenReportException {

        addStaticResources();
        createHead();
        createBody();

        flushAndCloseSink();
    }

    private void createHead() {
        sink.head();
        sink.title();
        sink.text(bundle.getString("report.mafia.title"));
        sink.title_();
        sink.rawText("<link rel='stylesheet' type='text/css' href='./css/fitnesse.css' media='screen'/>");
        sink.rawText("<link rel='stylesheet' type='text/css' href='./css/fitnesse_print.css' media='print'/>");
        sink.rawText("<script src='./javascript/fitnesse.js' type='text/javascript'></script>");
        sink.head_();

    }

    private void createBody() {
        sink.body();

        createIntroduction();
        createSummary();
        createFileList();
        createDetails();

        sink.body_();
    }

    private void createIntroduction() {
        sink.section1();
        sink.sectionTitle1();
        sink.text(bundle.getString("report.mafia.title"));
        sink.sectionTitle1_();

        sink.paragraph();
        sink.text(bundle.getString("report.mafia.moreInfo") + " ");
        sink.link(bundle.getString("report.mafia.moreInfo.link"));
        sink.text(bundle.getString("report.mafia.moreInfo.linkName"));
        sink.link_();
        sink.text(".");

        sink.paragraph_();
        sink.section1_();
    }

    private void createSummary() {
        final TestSummary testSummary = getTestSummary();
        sink.section1();
        sink.sectionTitle1();
        sink.text(bundle.getString("report.mafia.summary"));
        sink.sectionTitle1_();

        sink.table();

        sink.tableRow();
        sink.tableCell();
        sink.text(bundle.getString("report.mafia.success"));
        sink.tableCell_();
        sink.tableCell();
        sink.text("" + testSummary.getRight());
        sink.nonBreakingSpace();
        iconSuccess();
        sink.tableCell_();
        sink.tableRow_();

        sink.tableRow();
        sink.tableCell();
        sink.text(bundle.getString("report.mafia.error"));
        sink.tableCell_();
        sink.tableCell();
        sink.text("" + testSummary.getWrong());
        sink.nonBreakingSpace();
        iconError();
        sink.tableCell_();
        sink.tableRow_();

        sink.tableRow();
        sink.tableCell();
        sink.text(bundle.getString("report.mafia.exception"));
        sink.tableCell_();
        sink.tableCell();
        sink.text("" + testSummary.getExceptions());
        sink.nonBreakingSpace();
        iconException();
        sink.tableCell_();
        sink.tableRow_();

        sink.tableRow();
        sink.tableCell();
        sink.text(bundle.getString("report.mafia.ignore"));
        sink.tableCell_();
        sink.tableCell();
        sink.text("" + testSummary.getIgnores());
        sink.nonBreakingSpace();
        iconIgnore();
        sink.tableCell_();
        sink.tableRow_();

        sink.table_();
        sink.section1_();
    }

    private TestSummary getTestSummary() {
        final TestSummary testSummary = new TestSummary();
        for (final MafiaTestResult mafiaTestResult : mafiaTestResults) {
            testSummary.exceptions += mafiaTestResult.getTestResultRecord().exceptions;
            testSummary.ignores += mafiaTestResult.getTestResultRecord().ignores;
            testSummary.right += mafiaTestResult.getTestResultRecord().right;
            testSummary.wrong += mafiaTestResult.getTestResultRecord().wrong;
        }
        return testSummary;
    }

    private void createFileList() {
        sink.section1();
        sink.sectionTitle1();
        sink.text(bundle.getString("report.mafia.resultList.summary"));
        sink.sectionTitle1_();

        sink.table();
        sink.tableRow();
        sink.tableHeaderCell();
        sink.text(bundle.getString("report.mafia.resultList.summary"));
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text(bundle.getString("report.mafia.success").substring(0, 1));
        sink.nonBreakingSpace();
        iconSuccess();
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text(bundle.getString("report.mafia.error").substring(0, 1));
        sink.nonBreakingSpace();
        iconError();
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text(bundle.getString("report.mafia.exception").substring(0, 1));
        sink.nonBreakingSpace();
        iconException();
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text(bundle.getString("report.mafia.ignore").substring(0, 1));
        sink.nonBreakingSpace();
        iconIgnore();
        sink.tableHeaderCell_();
        sink.tableRow_();

        for (final MafiaTestResult mafiaTestResult : mafiaTestResults) {

            sink.tableRow();
            sink.tableCell();
            sink.link("#" + mafiaTestResult.getPageName());
            sink.text(mafiaTestResult.getPageName());
            sink.link_();
            sink.tableCell_();
            sink.tableCell();
            sink.text("" + mafiaTestResult.getTestResultRecord().getRight());
            sink.tableCell_();
            sink.tableCell();
            sink.text("" + mafiaTestResult.getTestResultRecord().getWrong());
            sink.tableCell_();
            sink.tableCell();
            sink.text("" + mafiaTestResult.getTestResultRecord().getExceptions());
            sink.tableCell_();
            sink.tableCell();
            sink.text("" + mafiaTestResult.getTestResultRecord().getIgnores());
            sink.tableCell_();
            sink.tableRow_();
        }
        sink.table_();
        sink.section1_();
    }

    private void createDetails() {
        sink.section1();
        sink.sectionTitle1();
        sink.text(bundle.getString("report.mafia.resultDetails.summary"));
        sink.sectionTitle1_();

        for (final MafiaTestResult mafiaTestResult : mafiaTestResults) {
            sink.anchor(mafiaTestResult.getPageName());
            sink.rawText(mafiaTestResult.getHtmlResult());
            sink.anchor_();
        }
        sink.section1_();
    }

    private void flushAndCloseSink() {
        sink.flush();
        sink.close();
    }

    private void iconIgnore() {
        sink.figure();
        sink.figureCaption();
        sink.text(bundle.getString("report.mafia.ignore.info"));
        sink.figureCaption_();
        sink.figureGraphics("images/icon_ignore_sml.gif");
        sink.figure_();
    }

    private void iconException() {
        sink.figure();
        sink.figureCaption();
        sink.text(bundle.getString("report.mafia.exception.info"));
        sink.figureCaption_();
        sink.figureGraphics("images/icon_exception_sml.gif");
        sink.figure_();
    }

    private void iconError() {
        sink.figure();
        sink.figureCaption();
        sink.text(bundle.getString("report.mafia.error.info"));
        sink.figureCaption_();
        sink.figureGraphics("images/icon_error_sml.gif");
        sink.figure_();
    }

    private void iconSuccess() {
        sink.figure();
        sink.figureCaption();
        sink.text(bundle.getString("report.mafia.success.info"));
        sink.figureCaption_();
        sink.figureGraphics("images/icon_success_sml.gif");
        sink.figure_();
    }

    private void addStaticResources() throws MavenReportException {

        final ReportResource rresource = new ReportResource(PLUGIN_RESOURCES, outputDirectory);
        try {
            rresource.copy("images/icon_error_sml.gif");
            rresource.copy("images/icon_exception_sml.gif");
            rresource.copy("images/icon_ignore_sml.gif");
            rresource.copy("images/icon_success_sml.gif");
            rresource.copy("images/collapsableClosed.gif");
            rresource.copy("images/collapsableOpen.gif");
            rresource.copy("javascript/fitnesse.js");
            rresource.copy("css/fitnesse.css");

        } catch (final IOException e) {
            throw new MavenReportException("Unable to copy static resources.", e);
        }
    }
}

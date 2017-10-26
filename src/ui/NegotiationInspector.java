package ui;import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class NegotiationInspector extends ApplicationFrame {

   public NegotiationInspector( String applicationTitle , String chartTitle ) {
      super(applicationTitle);
      JFreeChart lineChart = ChartFactory.createLineChart(
         chartTitle,
         "Iteration","Price",
         createDataset(),
         PlotOrientation.VERTICAL,
         true,true,false);
         
      ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      setContentPane( chartPanel );
   }

   private DefaultCategoryDataset createDataset( ) {
      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
      dataset.addValue( 15 , "Price" , "1970" );
      dataset.addValue( 30 , "Price" , "1980" );
      dataset.addValue( 60 , "Price" ,  "1990" );
      dataset.addValue( 120 , "Price" , "2000" );
      dataset.addValue( 240 , "Price" , "2010" );
      dataset.addValue( 300 , "Price" , "2014" );
      return dataset;
   }
   
   public static void main( String[ ] args ) {
      NegotiationInspector chart = new NegotiationInspector(
         "Price Vs Iteration" ,
         "Numer of Iterations");

      chart.pack( );
      RefineryUtilities.centerFrameOnScreen( chart );
      chart.setVisible( true );
   }
}
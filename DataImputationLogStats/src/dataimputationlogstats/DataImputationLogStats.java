package dataimputationlogstats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author kostis
 */
public class DataImputationLogStats {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        // TODO code application logic here
        
        
        String currentDir = System.getProperty("user.dir");
       
        String alg = args[0];
        String input = args[1];
        String scen = args[2];
        Integer perc = 4;
        if(scen.equals("mcar"))
        {
            perc=100;
        }
        else if(scen.equals("miss_disj"))
        {
            String[] input_split = input.split("_");
            int length = Integer.parseInt(input_split[1]);
            perc =  (int) length/10;
            perc = (int) length/perc;
           
        }
        else
        {
            String[] input_split = input.split("_");
            int length = Integer.parseInt(input_split[1]);
            perc =  (int) length/10;
            
        }
        String miss_perc = perc.toString();
        String root = currentDir + "/" +  "/" + alg + "/" + scen + "/" + input + "/";
        System.out.println(root);
        String output_path = root + "recovery/values/" + miss_perc + "/";
        String output_type;
        String output_file;
        if(alg.equals("cdrec"))
        {
         output_type = "cdrec" + miss_perc + "_k3.txt";
        }
        else if(alg.equals("dynammo"))
        {
          output_type = "dynammo" + miss_perc + ".txt";
        }
        else
        {
          System.out.println("Wrong algorithm argument");
          output_type= "";
          System.exit(0);
        }       
        
        output_file = output_path + output_type;
        System.out.println(root);
        
        
        BufferedReader reader = new BufferedReader(new FileReader(output_file));
        String line;
        int i=0;
        int j=0;
        while ((line = reader.readLine()) != null) 
        {
            String[] line3 = line.split("	");
            String[] line2 = line3[1].split(" ");
            //one[i] = Float.parseFloat(line2[0]);
            i++;
            j = line2.length;
        }
        
        reader.close();
        reader = new BufferedReader(new FileReader(output_file));
        double[][] durations = new double[i][j];
        boolean[][] durations_boolean = new boolean[i][j];
        boolean durations_correct[][] = new boolean[j][i];
        HashMap<Integer,ArrayList<Double>> duration = new HashMap();
        i=0;
        while ((line = reader.readLine()) != null) 
        {
            String[] line3 = line.split("	");
            String[] line2 = line3[1].split(" ");
            ArrayList<Double> temp_array = new ArrayList();
            
            for(j=0;j<line2.length;j++)
            {
             durations[i][j] = Double.parseDouble(line2[j]);
             temp_array.add(Double.parseDouble(line2[j]));
             
            }
            duration.put(i, temp_array);
            i++;
            
        }
        
        i=0;
        
        
       
        String imputed_path = root + "recovery/values/recovered_matrices/" + "recoveredMat" + miss_perc + ".txt";
       
        reader = new BufferedReader(new FileReader(imputed_path));
        while ((line = reader.readLine()) != null) 
        {
            
            String[] line2 = line.split(" ");
            
            i++;
            
        }
        
        reader.close();
        reader = new BufferedReader(new FileReader(imputed_path));
        HashMap<Double,Integer> imputed_values = new HashMap();
        Double[][] reference = new Double[i][2];
        Double[][] reference_replace = new Double[i][2];
        Boolean[][] replace = new Boolean[i][2];
        i=0;
        while ((line = reader.readLine()) != null) 
        {
            
            String[] line2 = line.split(" ");
            if(!imputed_values.containsKey(Double.parseDouble(line2[1])))
            {
                imputed_values.put(Double.parseDouble(line2[1]),1);
            }
            else
            {
                int count_value = imputed_values.get(Double.parseDouble(line2[1]));
                count_value +=1;
                imputed_values.put(Double.parseDouble(line2[1]),count_value);
            }
            
            reference[i][0]=Double.parseDouble(line2[0]);
            reference[i][1]=Double.parseDouble(line2[1]);
            reference_replace[i][0]=Double.parseDouble(line2[0]);
            reference_replace[i][1]=Double.parseDouble(line2[1]);
            i++;
            
        }
        reader.close();
        i=0;
        j=0;
        int sum_cv=0;
        int sum_length=0;
        int number_missing[] = new int[10];
        int i_zero=0;
        int j_zero=0;
        Iterator it = duration.entrySet().iterator();
        HashMap<Double,HashSet<Double>> association = new HashMap(); // antistoixisi mesou orou me tis times pou tha antikatatastisei
        
        String index = root + "index/";
        
        String index_path;
        if(scen.equals("mcar") || scen.equals("miss_disj"))
        {
          index_path = index + "index"  + ".txt";
        }
        else
        {
          index_path = index + "index" + miss_perc + ".txt";
        }
        
        reader = new BufferedReader(new FileReader(index_path));
        
        while ((line = reader.readLine()) != null) 
        {
            
            String[] line2 = line.split(",");
            
            String line2_temp1 = line2[0].replace("(","");
            Integer index1 = Integer.parseInt(line2_temp1);
            String line2_temp2 = line2[1].replace(")","");
            line2_temp2 = line2_temp2.replace(" ", "");
            
            Integer index2 = Integer.parseInt(line2_temp2);
            
            
            
            durations_boolean[index2][index1] = true;
            durations_correct[index1][index2] = true;
            
        }
       
        
        reader.close();
        int a=0;
        int count=0;
        while(it.hasNext())
        {
            int b=0;
            Map.Entry<Integer,ArrayList<Double>> output2 = (Map.Entry<Integer,ArrayList<Double>>) it.next();
            ArrayList<Double> temp_array2 = output2.getValue();
            int og_length = temp_array2.size();
            Iterator it2 = temp_array2.iterator();
            HashSet<Double> removed = new HashSet();
            boolean check_zero = false;
            while(it2.hasNext())
            {
                Double num = (Double) it2.next();
                
                if(imputed_values.containsKey(num))
                {
                    
                    if( durations_boolean[a][b]==true && durations[a][b]==num)
                    {
                        count+=1;
                        int count_imputed = imputed_values.get(num);
                        count_imputed = count_imputed - 1;
                        if(count_imputed==0)
                        {
                        imputed_values.remove(num);
                        }
                        removed.add(num);
                        
                    }
                    else if(num < 0)
                    {
                        removed.add(num);
                       
                    }
                    
                    
                    
                }
             j++;
             b++;
            }
            Iterator it_out = removed.iterator();
            while(it_out.hasNext())
            {
                Double remove = (Double) it_out.next();
                temp_array2.remove(remove);
            }
            j=0;
            int final_length = temp_array2.size();
            Double[] test_final = new Double[temp_array2.size()];
            temp_array2.toArray(test_final);
            Double mean = calculateMean(test_final);
            Double cv = calculateSD(test_final)/mean;
            
            if(cv<0.3)
            {
              sum_cv+=1;
             
              association.put(mean, removed);
            }
             
            sum_length += (Math.abs(og_length-final_length));
            i++;
            a++;
        } 

       
        
        Iterator it4 = association.entrySet().iterator();
        int test_final_square=0;
        while(it4.hasNext())
        {
            Map.Entry<Double,HashSet<Double>> output_poios = (Map.Entry<Double,HashSet<Double>>) it4.next();
            Double replacement_value = output_poios.getKey();
            HashSet<Double> values_replaced = output_poios.getValue();
            for(Double num: values_replaced)
            {
                for(i=0;i<reference.length;i++)
                {
                  if(num.equals(reference[i][1]))
                  {
                      
                      test_final_square+=1;
                      if(Math.abs(reference[i][1]-reference[i][0])>Math.abs(replacement_value-reference[i][0]))
                      {
                          if(replacement_value<0)
                          {
                              
                          }
                          
                       
                      reference_replace[i][1] = replacement_value;
                     
                      }
                      
                  }  
                }
            }
        }
        
        Double mae_og=0.0;
        Double mae_after=0.0;
        Double mape_og= 0.0;
        Double mape_after = 0.0;
        for(i=0;i<reference.length;i++)
        {
            if(reference[i][0]==0)
            {
                
                mae_og += Math.abs(reference[i][1]-reference[i][0]);
                mae_after += Math.abs(reference_replace[i][1] - reference[i][0]);
                mape_og += Math.abs(reference[i][1]-reference[i][0]);
                mape_after += Math.abs(reference_replace[i][1] - reference[i][0]);
            }
            else{
              mape_og += Math.abs((reference[i][1]-reference[i][0])/((reference[i][0])));
            mape_after += Math.abs((reference_replace[i][1]-reference_replace[i][0])/((reference_replace[i][0])));
             mae_og += Math.abs((reference[i][1]-reference[i][0]));
            mae_after += Math.abs((reference_replace[i][1]-reference_replace[i][0]));  
            
            }
        }
        System.out.println("mae(before): " + (Double)mae_og/reference.length + " , " + "mae(after): " + (Double)mae_after/reference.length);
        System.out.println("mape(before): " + (Double)mape_og/reference.length + " , " + "mape(after)" + (Double)mape_after/reference.length);
        
        for( i=0;i<number_missing.length;i++)
        {
            
        }
        int count22=0;
        for(i=0;i<durations_correct.length;i++)
        {
            for(j=0;j<durations_correct[0].length;j++)
            {
                if(durations_correct[i][j])
                {
                    
                    count22+=1;
                }
            }
        }
        
        
        
        
        
        
        
        
        for(i=0;i<durations.length;i++)
        {
            String line3="";
            for(int k=0;k<durations[i].length;k++)
            {
                //line3+=" ";
                //line3+=durations[i][k];
            }
            
            
        }
        
        
        Iterator it3 = duration.entrySet().iterator();
        i=0;
        while(it3.hasNext())
        {
            Map.Entry<Integer,ArrayList<Double>> output = (Map.Entry<Integer,ArrayList<Double>>)it3.next();
            
        }
        
    }
    
    public static double calculateSD(Double numArray[])
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for(double num : numArray) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
    }
    
    public static double calculateMean(Double numArray[])
    {
        //System.out.println("length:" + numArray.length);
        double length = numArray.length;
        double sum =0;
        for(double num : numArray){
            sum+=num;
        }
        double mean = sum/length;
        return mean;
    }
    
}

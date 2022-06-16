/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deepmvistats;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Κωστής
 */
public class DeepMVIStats {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        // TODO code application logic here
        
        String currentDir = System.getProperty("user.dir");
        currentDir = currentDir + "/";
        
        String input_file = args[0];
        String dataset = args[1];
        String output = args[2];
        String input_path = currentDir + input_file;
        BufferedReader reader = new BufferedReader(new FileReader(input_path));
        String line;
        int i=0;
        int j=0;
        while ((line = reader.readLine()) != null) 
        {
            
            String[] line2 = line.split(" ");
            
            i++;
            j = line2.length;
        }
        
        reader.close();
        reader = new BufferedReader(new FileReader(input_path));
        double[][] durations_missing = new double[i][j];
        double[][] durations_output = new double[i][j];
        double[][] durations_reference = new double[i][j];
        boolean[][] missing = new boolean[i][j];
        boolean[][] missing2 = new boolean[i][j];
        
        HashMap<Integer,ArrayList<Double>> duration = new HashMap();
        i=0;
        int countf=0;
        while ((line = reader.readLine()) != null) 
        {
            
            String[] line2 = line.split(" ");
            ArrayList<Double> temp_array = new ArrayList();
            
            for(j=0;j<line2.length;j++)
            {
             Double temp = Double.parseDouble(line2[j]);
             durations_missing[i][j] = Double.parseDouble(line2[j]);
             
             
              temp_array.add(Double.parseDouble(line2[j]));
             if(temp.equals(Double.NaN))
                     {
                         missing[i][j]=true;
                         missing2[i][j]=true;
                         countf+=1;
                     }
            }
            duration.put(i, temp_array);
            i++;
            
        }
        
        
        i=0;
        String output_name = currentDir+ output;
        reader = new BufferedReader(new FileReader(output_name));
        while ((line = reader.readLine()) != null) 
        {
            
            String[] line2 = line.split(" ");
           
            
            for(j=0;j<line2.length;j++)
            {
             
             durations_output[i][j] = Double.parseDouble(line2[j]);
            } 
            
            i++;
            
        }
        reader.close();
        i=0;
       
        String dataset_path = currentDir + dataset;
        reader = new BufferedReader(new FileReader(dataset_path));// input_normal
        while ((line = reader.readLine()) != null) 
        {
            
            String[] line2 = line.split(" ");
           
            
            for(j=0;j<line2.length;j++)
            {
             
             durations_reference[i][j] = Double.parseDouble(line2[j]);
             
            } 
            
            i++;
            
        }
        i=0;
        reader.close();
        double mae=0;
        double mape =0;
        double mae_perc=0;
         for(i=0;i<durations_missing.length;i++)
               {
                   for(j=0;j<durations_missing[0].length;j++)
                   {
                       if(missing[i][j])
                       {
                          if(durations_reference[i][j]!=0)
                          {
                          mape += (Math.abs(durations_output[i][j] - durations_reference[i][j]))/durations_reference[i][j]; 
                          mae += (Math.abs(durations_output[i][j] - durations_reference[i][j]));
                          }
                          else
                          {
                           mape += Math.abs(durations_output[i][j] - durations_reference[i][j]);
                           mae += (Math.abs(durations_output[i][j] - durations_reference[i][j]));
                          }
                       }
                   }
               }
         
        
        i=0;
        reader.close();
        int sum_cv=0;
        HashMap<Double,HashSet<Double>> association = new HashMap();
        for( i=0;i<duration.size();i++)
        {
            ArrayList<Double> temp_array = duration.get(i);
            Iterator it = temp_array.iterator();
            HashSet<Double> removed = new HashSet();
            while(it.hasNext())
            {
                Double num = (Double) it.next();
                if(num.equals(Double.NaN))
                {
                    
                    removed.add(num);
                    it.remove();
                }
            }
            
            Double[] test_final = new Double[temp_array.size()];
            temp_array.toArray(test_final);
            Double mean = calculateMean(test_final);
            Double cv = calculateSD(test_final)/mean;
            if(calculateSD(test_final)==0.0 && mean==0.0)
            {
                cv=0.4;
            }
            
            if(cv<=0.5)
            {
             association.put(mean, removed);
            }
            
        }
        
        Iterator it2 = association.entrySet().iterator();
        int count_replace =0;
        int count2 =0;
        while(it2.hasNext())
        {
            Map.Entry<Double,HashSet<Double>> output_poios = (Map.Entry<Double,HashSet<Double>>) it2.next();
            Double replacement_value = output_poios.getKey();//mean toy array
            HashSet<Double> values_replaced = output_poios.getValue();//removed apo to array poy exei ginei associate me to mean
            for(Double num : values_replaced)
            {
               for(i=0;i<durations_missing.length;i++)
               {
                   for(j=0;j<durations_missing[0].length;j++)
                   {
                       if(num.equals(durations_missing[i][j]) && missing[i][j])
                       {
                           ArrayList<Double> check = duration.get(i);
                           Double[] check_array = new Double[check.size()];
                           check.toArray(check_array);
                           Double check_mean = calculateMean(check_array);
                           if(check_mean.equals(replacement_value))
                           {
                           count_replace+=1;
                           
                           //if(Math.abs(durations_output[i][j] - durations_reference[i][j])>Math.abs(replacement_value-durations_reference[i][j]))
                           //{
                            durations_output[i][j] = replacement_value;
                            missing[i][j]=false;
                           //}
                           }
                       }
                       
                       
                   }
               }
            }
        }
         double mae2=0;
         double mape2 =0;
         for(i=0;i<durations_missing.length;i++)
               {
                   for(j=0;j<durations_missing[0].length;j++)
                   {
                       if(missing2[i][j])
                       {
                          if(durations_reference[i][j]!=0)
                          {
                          mape2 += (Math.abs(durations_output[i][j] - durations_reference[i][j]))/durations_reference[i][j];
                          mae2 += (Math.abs(durations_output[i][j] - durations_reference[i][j]));
                          }
                          else
                          {
                               mape2 += Math.abs(durations_output[i][j] - durations_reference[i][j]);
                               mae2 += (Math.abs(durations_output[i][j] - durations_reference[i][j]));
                          }
                          if(durations_reference[i][j]!=0)
                          {
                          mae_perc+= Math.abs(durations_output[i][j] - durations_reference[i][j]);
                          if(Math.abs(durations_output[i][j] - durations_reference[i][j])/(durations_reference[i][j])>10000)
                          {
                              
                          }
                          }
                          
                       }
                   }
               }
         
         System.out.println("MAE (original): " + mae/countf +" , MAE(post-processing): " + mae2/countf);
         System.out.println("MAPE (original): "+ mape/countf + " ,  MAPE(post-processing):" + mape2/countf);
         
        
        
        
        
        
        
        
        
        
       
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
        double length = numArray.length;
        double sum =0;
        for(double num : numArray){
            sum+=num;
        }
        double mean = sum/length;
        return mean;
    }
    
}

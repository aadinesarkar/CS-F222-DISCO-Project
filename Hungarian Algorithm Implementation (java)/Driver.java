import java.io.*;
import java.util.*;
/**
 * This is the Driver code for the programme.
 * After populating input and output files (see README.md) and compiling *.java files in this directory, run this class.
 * @author f20210967@goa.bits-pilani.ac.in
 * @author f20220091@goa.bits-pilani.ac.in
 * @author f20220260@goa.bits-pilani.ac.in
 */
public class Driver
{
    public static void main(String[] args)throws IOException
    {
        String inputFileName, courseFileName;
        if(args.length==0)//testing
        {
            inputFileName="data/preferences/sample.csv";
            courseFileName="data/courses/sample_courses.csv";
        }
        else if(args.length==1)//For CS department
        {
            inputFileName="data/preferences/"+args[0];
            courseFileName="data/courses/sample_courses.csv";
        }
        else if(args.length==2)//For other departments
        {
            inputFileName="data/preferences/"+args[0];
            courseFileName="data/courses/"+args[1];
        }
        else
        {
            System.out.println("Too many arguments! Attempting to read instructor preferences from "+args[0]+" and courses from "+args[1]);
            inputFileName="data/preferences/"+args[0];
            courseFileName="data/courses/"+args[1];
        }

        String shortName, outputFileName;
        shortName=(inputFileName.split("/"))[2];
        outputFileName="data/output/output_"+shortName;

        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName));
        //header
        Prof[] profnames=AUGSD.readInput(inputFileName, new Course[0], new Course[0], new Course[0], new Course[0]);
        for(int i=0; i<profnames.length; i++)
        {
            if(i<profnames.length-1)//all but last prof
            {
                bw.write(profnames[i].getName()+" [x"+profnames[i].getLoad()+"],");
            }
            else//last prof (no comma)
            {
                bw.write(profnames[i].getName()+" [x"+profnames[i].getLoad()+"]");
            }
        }
        bw.write("\n");

        ArrayList<Integer> keys=new ArrayList<Integer>();
        ArrayList<String> values=new ArrayList<String>();//why not a TreeMap? Because we need duplicate keys to handle multiple optimal matchings

        //print without reverse
        int[] arr=new int[AUGSD.readInput(inputFileName, new Course[0], new Course[0], new Course[0], new Course[0]).length];
        for(int i=0; i<arr.length; i++)
        {
            arr[i]=i;
        }
        int n=arr.length;

        for(int i=0; i<(n+1)/2; i++)
        {
            Course[] fdcdc = Course.readCourseFile(courseFileName, CourseTypes.FD_CDC);
            Course[] hdcdc = Course.readCourseFile(courseFileName, CourseTypes.HD_CDC);
            Course[] fdelec = Course.readCourseFile(courseFileName, CourseTypes.FD_Elec);
            Course[] hdelec = Course.readCourseFile(courseFileName, CourseTypes.HD_Elec);

            Prof[] profs = AUGSD.readInput(inputFileName, fdcdc, hdcdc, fdelec, hdelec);

            Prof[] permutedprofs = new Prof[profs.length];

            for(int j=0; j<permutedprofs.length; j++)
            {
                permutedprofs[j]=profs[arr[j]];
            }
            
            AUGSD bitsgoa=new AUGSD(permutedprofs, fdcdc, hdcdc, fdelec, hdelec);
            bitsgoa.match(false);
            //bitsgoa.printMatching();
            //System.out.println(bitsgoa.getMetric());
            keys.add(Integer.valueOf(bitsgoa.getMetric()));
            values.add(bitsgoa.makeOutput(arr));

            //above this point arr is how it should be
            int temp1=arr[(n/2)-1];
            int temp2=arr[n/2];
            for(int j=(n/2)-1; j>=1; j--)
                arr[j]=arr[j-1];
            arr[0]=temp1;
            for(int j=n/2; j<n-1; j++)
                arr[j]=arr[j+1];
            arr[n-1]=temp2;
            arr[0]=temp1;
        }




        //print with reverse
        arr=new int[AUGSD.readInput(inputFileName, new Course[0], new Course[0], new Course[0], new Course[0]).length];
        for(int i=0; i<arr.length; i++)
        {
            arr[i]=i;
        }
        n=arr.length;

        for(int i=0; i<(n+1)/2; i++)
        {
            Course[] fdcdc = Course.readCourseFile(courseFileName, CourseTypes.FD_CDC);
            Course[] hdcdc = Course.readCourseFile(courseFileName, CourseTypes.HD_CDC);
            Course[] fdelec = Course.readCourseFile(courseFileName, CourseTypes.FD_Elec);
            Course[] hdelec = Course.readCourseFile(courseFileName, CourseTypes.HD_Elec);

            Prof[] profs = AUGSD.readInput(inputFileName, fdcdc, hdcdc, fdelec, hdelec);

            Prof[] permutedprofs = new Prof[profs.length];

            for(int j=0; j<permutedprofs.length; j++)
            {
                permutedprofs[j]=profs[arr[j]];
            }
            
            AUGSD bitsgoa=new AUGSD(permutedprofs, fdcdc, hdcdc, fdelec, hdelec);
            bitsgoa.match(true);
            //bitsgoa.printMatching();
            //System.out.println(bitsgoa.getMetric());
            keys.add(Integer.valueOf(bitsgoa.getMetric()));
            values.add(bitsgoa.makeOutput(arr));

            //above this point arr is how it should be
            int temp1=arr[(n/2)-1];
            int temp2=arr[n/2];
            for(int j=(n/2)-1; j>=1; j--)
                arr[j]=arr[j-1];
            arr[0]=temp1;
            for(int j=n/2; j<n-1; j++)
                arr[j]=arr[j+1];
            arr[n-1]=temp2;
            arr[0]=temp1;
        }

        Integer[] keyArr=new Integer[keys.size()];
        String[] valueArr=new String[values.size()];

        for(int i=0; i<keys.size(); i++)
        {
            keyArr[i]=keys.get(i);
            valueArr[i]=values.get(i);
        }
        
        for(int i=0; i<keyArr.length; i++)//bye bye runtime
        {
            for(int j=0; j<keyArr.length-1; j++)
            {
                if(keyArr[j]<keyArr[j+1])
                {
                    Integer tempKey=keyArr[j+1];
                    keyArr[j+1]=keyArr[j];
                    keyArr[j]=tempKey;

                    String tempValue=valueArr[j+1];
                    valueArr[j+1]=valueArr[j];
                    valueArr[j]=tempValue;
                }
            }
        }

        for(String output: valueArr)
        {
            bw.write(output);
        }

        bw.close();
        System.out.println("Success! Solutions stored at "+outputFileName);
    }
}
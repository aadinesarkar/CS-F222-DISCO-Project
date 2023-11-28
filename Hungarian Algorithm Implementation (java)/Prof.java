public class Prof
{
    private String name;
    private Course[] FD_CDCprefList;
    private Course[] HD_CDCprefList;
    private Course[] FD_ElecPrefList;
    private Course[] HD_ElecPrefList;
    private Course[] courses;
    private final int load;

    /**
     * 
     * @param name instructor's name. case, whitespace and punctuation sensitive
     * @param preferenceList array of courses in order of preference
     * @param load maximum total load from all courses, considering load from half a course is 1.
     */
    public Prof(String name, int load, Course[] FD_CDCprefList, Course[] HD_CDCprefList, Course[] FD_ElecPrefList, Course[] HD_ElecPrefList)
    {
        this.name=name;
        this.load=load;
        this.FD_CDCprefList=FD_CDCprefList;
        this.HD_CDCprefList=HD_CDCprefList;
        this.FD_ElecPrefList=FD_ElecPrefList;
        this.HD_ElecPrefList=HD_ElecPrefList;
        courses=new Course[0];
    }

    /**
     * @return the instructor's name. case, whitespace and punctuation sensitive
     */
    public String getName()
    {
        return name;
    }

    /**
     * Adds course to corresponding preference list of the instructor.
     * @param course the course to be added
     * @return 0 if successful, -1 otherwise
     */
    public int addPreference(Course course)
    {
        Course[] temp;
        switch(course.getType())
        {
            case FD_CDC:
                temp=new Course[FD_CDCprefList.length+1];
                for(int i=0; i<FD_CDCprefList.length; i++)
                {
                    temp[i]=FD_CDCprefList[i];
                }
                temp[FD_CDCprefList.length]=course;
                FD_CDCprefList=temp;
                return 0;
            case HD_CDC:
                temp=new Course[HD_CDCprefList.length+1];
                for(int i=0; i<HD_CDCprefList.length; i++)
                {
                    temp[i]=HD_CDCprefList[i];
                }
                temp[HD_CDCprefList.length]=course;
                HD_CDCprefList=temp;
                return 0;
            case FD_Elec:
                temp=new Course[FD_ElecPrefList.length+1];
                for(int i=0; i<FD_ElecPrefList.length; i++)
                {
                    temp[i]=FD_ElecPrefList[i];
                }
                temp[FD_ElecPrefList.length]=course;
                FD_ElecPrefList=temp;
                return 0;
            case HD_Elec:
                temp=new Course[HD_ElecPrefList.length+1];
                for(int i=0; i<HD_ElecPrefList.length; i++)
                {
                    temp[i]=HD_ElecPrefList[i];
                }
                temp[HD_ElecPrefList.length]=course;
                HD_ElecPrefList=temp;
                return 0;
            default:
                return -1;
        }
    }

    /**
     * @return an ordered array containing the instructor's preferred courses of the specified type
     * @param type the type of course for which a preference list is desired
     */
    public Course[] getPrefList(CourseTypes type)
    {
        switch(type)
        {
            case FD_CDC:
                return FD_CDCprefList;
            case HD_CDC:
                return HD_CDCprefList;
            case FD_Elec:
                return FD_ElecPrefList;
            case HD_Elec:
                return HD_ElecPrefList;
            default:
                return new Course[]{};
        }
    }

    /**
     * Useful for printing.
     * @return a string containing 4 lists of courses the instructor prefers
     */
    public String getPreferenceListsString()//useful for printing
    {
        String pl="FD CDCs [";
        for(int i=0; i<FD_CDCprefList.length; i++)
        {
            pl=pl+FD_CDCprefList[i].getCode()+" ("+FD_CDCprefList[i].getType()+")       ";
        }
        pl=pl+"]\nHD CDCs [";

        for(int i=0; i<HD_CDCprefList.length; i++)
        {
            pl=pl+HD_CDCprefList[i].getCode()+" ("+HD_CDCprefList[i].getType()+")       ";
        }
        pl=pl+"]\nFD Elecs [";

        for(int i=0; i<FD_ElecPrefList.length; i++)
        {
            pl=pl+FD_ElecPrefList[i].getCode()+" ("+FD_ElecPrefList[i].getType()+")       ";
        }
        pl=pl+"]\nHD Elecs [";

        for(int i=0; i<HD_ElecPrefList.length; i++)
        {
            pl=pl+HD_ElecPrefList[i].getCode()+" ("+HD_ElecPrefList[i].getType()+")       ";
        }
        pl=pl+"]";
        return pl;
    }

    /**
     * @return an array containing the courses assigned to the instructor
     */
    public Course[] getCourses()
    {
        return courses;
    }

    /**
     * @return the maximum total load that may come to the professor from all courses, considering that half a course adds a load of 1
     */
    public int getLoad()
    {
        return load;
    }

    /**
     * @param course the course to be assigned to the instructor
     * @return 0 if assignment was successful, -1 otherwise
     */
    public int assignCourse(Course course)
    {
        if(courses.length<load)
        {
            Course[] temp = new Course[courses.length+1];
            for(int i=0; i<courses.length; i++)
            {
                temp[i]=courses[i];
            }
            temp[courses.length]=course;
            courses=temp;
            return 0;
        }
        return -1;
    }

    /**
     * De-assigns ONE instance of the specified course from the instructor. If the same course has been assigned to the instructor twice, only one instance is removed.
     * @param course course to remove
     * @return 0 if successful, else -1
     */
    public int removeCourse(Course course)
    {
        int index=-1;
        for(int i=0; i<courses.length; i++)
        {
            if(courses[i].equals(course))
                index=i;
        }
        if(index==-1)
        {
            return -1;
        }
        Course[] temp=new Course[courses.length-1];
        for(int i=0; i<index; i++)
        {
            temp[i]=courses[i];
        }
        for(int i=index+1; i<courses.length; i++)
        {
            temp[i-1]=courses[i];
        }
        courses=temp;
        return 0;
    }

    public int getCourseCount()
    {
        return courses.length;
    }

    /**
     * @return whether the instructor has course slots left
     */
    public boolean isFree()
    {
        return getCourseCount()<load;
    }

    /**
     * @param profs an array of instructors
     * @return the index of the calling instructor in the array if found, -1 otherwise
     */
    public int getProfIndex(Prof[] profs)
    {
        for(int i=0; i<profs.length; i++)
        {
            if(this.equals(profs[i]))
                return i;
        }
        return -1;
    }

    public String toString()
    {
        return name;
    }

    public boolean equals(Object other)
    {
        return (other instanceof Prof)&&this.getName().equals(Prof.class.cast(other).getName());
    }
}
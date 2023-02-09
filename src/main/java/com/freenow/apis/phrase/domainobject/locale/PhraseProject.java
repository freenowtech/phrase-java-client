package com.freenow.apis.phrase.domainobject.locale;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhraseProject
{
    private String projectId;
    private List<PhraseBranch> branches;


    private PhraseProject(String projectId, List<PhraseBranch> branches)
    {
        this.projectId = projectId;
        this.branches = branches;
    }


    public String getProjectId()
    {
        return projectId;
    }


    public List<PhraseBranch> getBranches()
    {
        return branches;
    }


    public static PhraseProject.Builder newBuilder()
    {
        return new PhraseProject.Builder();
    }


    public static class Builder
    {
        private String projectId;
        private List<PhraseBranch> branches = new ArrayList<>();


        public PhraseProject.Builder withProjectId(String projectId)
        {
            this.projectId = projectId;
            return this;
        }


        public PhraseProject.Builder withBranches(List<PhraseBranch> branches)
        {
            this.branches = branches;
            return this;
        }


        public PhraseProject.Builder addBranch(final PhraseBranch branch)
        {
            branches.add(branch);
            return this;
        }


        public PhraseProject build()
        {
            return new PhraseProject(projectId, branches);
        }
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        PhraseProject that = (PhraseProject) o;
        return Objects.equals(projectId, that.projectId) &&
            Objects.equals(branches, that.branches);
    }


    @Override
    public int hashCode()
    {
        return Objects.hash(projectId, branches);
    }


    @Override
    public String toString()
    {
        return "PhraseProject{" +
            "projectId='" + projectId + '\'' +
            ", branches=" + branches +
            '}';
    }
}


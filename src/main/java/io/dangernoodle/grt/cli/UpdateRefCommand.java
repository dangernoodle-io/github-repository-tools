//package io.dangernoodle.grt.cli;
//
//import static io.dangernoodle.grt.Constants.GIT_REF_NAME;
//import static io.dangernoodle.grt.Constants.SHA1;
//import static io.dangernoodle.grt.Constants.UPDATE_REF;
//
//import java.util.Map;
//
//import com.beust.jcommander.Parameter;
//import com.beust.jcommander.Parameters;
//
//import io.dangernoodle.grt.Repository;
//import io.dangernoodle.grt.Workflow;
//import io.dangernoodle.grt.internal.FileLoader;
//import io.dangernoodle.grt.internal.UpdateRefWorkflow;
//import io.dangernoodle.grt.utils.RepositoryFactory;
//
//
//@Parameters(commandNames = UPDATE_REF, resourceBundle = "GithubRepositoryTools", commandDescriptionKey = "updateRef")
//public class UpdateRefCommand implements CommandLineParser.Command
//{
//    @Parameter(descriptionKey = "repo", required = true, names = "--repo")
//    private static String repo;
//
//    @Parameter(descriptionKey = "sha1", required = true)
//    private static String sha1;
//
//    @Parameter(descriptionKey = "ref", required = true)
//    private static String ref;
//
//    @Override
//    public Class<? extends CommandLineExecutor> getCommandExectorClass()
//    {
//        return Executor.class;
//    }
//
//    public static class Executor extends CommandLineExecutor.RepositoryExecutor
//    {
//        private UpdateRefWorkflow workflow;
//
//        public Executor(UpdateRefWorkflow workflow, FileLoader fileLoader, RepositoryFactory repositoryMerger)
//        {
//            super(fileLoader, repositoryMerger);
//            this.workflow = workflow;
//        }
//
//        @Override
//        protected void execute(Repository repository) throws Exception
//        {
//            workflow.execute(repository, new Workflow.Context(Map.of(SHA1, sha1, GIT_REF_NAME, ref)));
//        }
//
//        @Override
//        protected String getRepositoryName()
//        {
//            return repo;
//        }
//    }
//}

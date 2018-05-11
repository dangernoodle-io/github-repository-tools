package io.dangernoodle.grt.extensions;

import java.util.Collection;

import io.dangernoodle.grt.Repository;


public interface StatusCheckFactory
{
    Collection<String> getRequiredStatusChecks(String branch, Repository repository);
}

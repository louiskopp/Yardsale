package edu.hope.cs.yardsale.Control;

import java.lang.Error;

import java.util.ArrayList;
import edu.hope.cs.yardsale.Model.Post;

public interface APIDelegate<T> {
  void onAPIDataSuccess(T value);
  void onAPIFailure(Error error);
}

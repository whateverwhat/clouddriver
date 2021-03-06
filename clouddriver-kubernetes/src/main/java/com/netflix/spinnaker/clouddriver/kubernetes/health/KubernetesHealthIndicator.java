/*
 * Copyright 2015 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.netflix.spinnaker.clouddriver.kubernetes.health;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import com.netflix.spectator.api.Registry;
import com.netflix.spinnaker.clouddriver.core.AccountHealthIndicator;
import com.netflix.spinnaker.clouddriver.kubernetes.security.KubernetesNamedAccountCredentials;
import com.netflix.spinnaker.clouddriver.security.AccountCredentialsProvider;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public class KubernetesHealthIndicator
    extends AccountHealthIndicator<KubernetesNamedAccountCredentials> {
  private static final String ID = "kubernetes";
  private final AccountCredentialsProvider accountCredentialsProvider;

  @Autowired
  public KubernetesHealthIndicator(
      Registry registry, AccountCredentialsProvider accountCredentialsProvider) {
    super(ID, registry);
    this.accountCredentialsProvider = accountCredentialsProvider;
  }

  @Override
  protected ImmutableList<KubernetesNamedAccountCredentials> getAccounts() {
    return accountCredentialsProvider.getAll().stream()
        .filter(a -> a instanceof KubernetesNamedAccountCredentials)
        .map(a -> (KubernetesNamedAccountCredentials) a)
        .collect(toImmutableList());
  }

  @Override
  protected Optional<String> accountHealth(KubernetesNamedAccountCredentials accountCredentials) {
    try {
      accountCredentials.getCredentials().getDeclaredNamespaces();
      return Optional.empty();
    } catch (RuntimeException e) {
      return Optional.of(e.getMessage());
    }
  }
}

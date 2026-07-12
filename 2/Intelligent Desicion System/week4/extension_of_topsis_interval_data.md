# Extension of TOPSIS for Decision-Making Problems with Interval Data: Interval Efficiency

**G.R. Jahanshahloo, F. Hosseinzadeh Lotfi, A.R. Davoodi**
*Department of Mathematics, Tehran Science and Research Campus, Islamic Azad University, Tehran, Iran*

*Mathematical and Computer Modelling 49 (2009) 1137–1142*

---

## Abstract

There are some methods for solving Multiple Criteria Decision-Making problems, of which one is the TOPSIS method. When data is nondeterministic like interval data, the method must be modified to show the correct result. In this research we present a new TOPSIS method for ranking DMUs with interval data yielding the interval score for each alternative, and in the end we show that when data is deterministic, our new method is the same as the conventional one.

**Keywords:** TOPSIS, Multiple criteria decision making, Interval data

---

## 1. Introduction

Decision-making is the process of finding the best option from all of the feasible alternatives. In almost all such problems the multiplicity of criteria for judging the alternatives is pervasive. These criteria usually conflict with each other so there may be no solution satisfying all criteria simultaneously. That is, for many such problems, the decision maker wants to solve a multiple criteria decision-making (MCDM) problem.

An MCDM problem with finite possibilities can be concisely expressed in matrix format as shown in Table 1. In this table, A₁, A₂, …, Aₘ are possible alternatives among which decision makers have to choose, C₁, C₂, …, Cₙ are criteria with which alternative performance are measured, x_ij is the rating of alternative *i* with respect to criterion *j*, and w_j is the weight of criterion *j*.

**Table 1: A typical multiple attribute decision problem**

| | Criterion 1 | Criterion 2 | … | Criterion n |
|---|---|---|---|---|
| Alternative 1 | x₁₁ | x₁₂ | … | x₁ₙ |
| Alternative 2 | x₂₁ | x₂₂ | … | x₂ₙ |
| ⋮ | ⋮ | ⋮ | ⋱ | ⋮ |
| Alternative m | xₘ₁ | xₘ₂ | … | xₘₙ |

There are several methods for solving MCDM problems [1,2]. One of them is TOPSIS (technique for order preference by similarity to an ideal solution) presented by Hwang and Yoon [1]. In this method the rank of units depends on the distance from ideal and negative-ideal. There exists a large amount of literature involving TOPSIS theory and applications. For example, Lai et al. applied the concept of TOPSIS on MODM problems [3]. Abo-Sinna and Amer extended TOPSIS methods for solving multi-objective large-scale nonlinear programming problems [4]. Moreover, Olson used the weights and some other norms to measure these distances [5]. Also Kuo et al. [6] and Shis et al. [7] have extended TOPSIS for group decision making.

In these researches it is assumed data are deterministic but in real life there may be some other types, for instance fuzzy data, ordinal data and interval data. In other words, the decision maker would prefer to say his/her point of view in these forms rather than a real number because of the uncertainty and the lack of certain data. Jahanshahloo et al. have presented the TOPSIS method for interval data [8]. Some researches have been published on the applications of TOPSIS method with fuzzy data [9–12].

In this paper we present another method for solving MCDM problems by TOPSIS method consisting of interval data. In this method the score of each alternative will be an interval number. We apply the approaches mentioned in [13–18] to compare the interval scores we have found. In Section 2 the original TOPSIS method is introduced and in Section 3 we present the MCDM problem with interval data then the new method is introduced. An empirical example is presented in Section 4 and the final section will be the conclusion.

---

## 2. TOPSIS Method

TOPSIS (technique for order preference by similarity to an ideal solution) method is presented in Chen and Hwang [5], with reference to Hwang and Yoon [1]. TOPSIS is a multiple criteria method to identify solutions from a finite set of alternatives. The basic principle is that the chosen alternative should have the shortest distance from the ideal solution and the farthest distance from the negative-ideal solution.

The procedure of TOPSIS can be expressed in a series of steps:

**Step 1.** Calculate the normalized decision matrix. The normalized value n_ij is calculated as:

$$n_{ij} = \frac{x_{ij}}{\sqrt{\sum_{i=1}^{m} x_{ij}^2}} \quad \text{for } i = 1, \ldots, m \text{ and } j = 1, \ldots, n$$

**Step 2.** Calculate the weighted normalized decision matrix. The weighted normalized value v_ij is calculated as:

$$v_{ij} = w_i n_{ij} \quad \text{for } i = 1, \ldots, m \text{ and } j = 1, \ldots, n$$

where w_i is the weight of the *i*th attribute or criterion, and Σwᵢ = 1. These weights can be introduced by a decision maker.

**Step 3.** Determine the positive-ideal and negative-ideal solution:

$$A^+ = \{(v_1^+, v_2^+, \ldots, v_n^+)\} = \{(\max v_{ij} \mid i \in O),\ (\min v_{ij} \mid i \in I)\}$$

$$A^- = \{(v_1^-, v_2^-, \ldots, v_n^-)\} = \{(\min v_{ij} \mid i \in O),\ (\max v_{ij} \mid i \in I)\}$$

where *O* is associated with benefit criteria, and *I* is associated with cost criteria.

**Step 4.** Calculate the separation measures, using the *n*-dimensional Euclidean distance. The separation of each alternative from the ideal solution is given as:

$$d_j^+ = \left[\sum_{i=1}^{n} (v_{ij} - v_i^+)^2\right]^{1/2} \quad \forall j$$

Similarly, the separation from the negative-ideal solution is given as:

$$d_j^- = \left[\sum_{i=1}^{n} (v_{ij} - v_i^-)^2\right]^{1/2} \quad \forall j$$

**Step 5.** Calculate the relative closeness to the ideal solution. The relative closeness of the alternative A_j with respect to A⁺ is defined as:

$$R_j = \frac{d_j^-}{d_j^- + d_j^+} \quad \text{for } j = 1, \ldots, m$$

Since d_j⁻ ≥ 0 and d_j⁺ ≥ 0, then clearly R_j ∈ [0, 1].

**Step 6.** Rank the preference order. For ranking alternatives using this index, we can rank them in decreasing order. The basic principle of the TOPSIS method is that the chosen alternative should have the "shortest distance" from the ideal solution and the "farthest distance" from the negative-ideal solution. The TOPSIS method introduces two "reference" points.

---

## 3. TOPSIS Method with Interval Data

Considering the fact that, in some cases, determining the exact value of the elements of decision matrix is difficult and, as a result, their values are considered as intervals, therefore, we try to extend TOPSIS for these interval data.

**Definition 1.** The number *B* is an interval number on the real line ℝ if it is expressed as:

$$B = [b^l, b^u] = \{b : b^l \leq b \leq b^u,\ b \in \mathbb{R}\}$$

If b^l = b^u then b is a real number.

Suppose A₁, A₂, …, Aₘ are *m* possible alternatives among which decision makers have to choose, C₁, C₂, …, Cₙ are criteria with which alternative performance is measured, x_ij is the rating of alternative Aᵢ with respect to criterion Cⱼ and is not known exactly, and only we know x_ij ∈ [x_ij^l, x_ij^u]. Table 2 shows an MCDM problem with interval data.

**Table 2: A typical multiple attribute decision matrix with interval rates**

| | Criterion 1 | Criterion 2 | … | Criterion n |
|---|---|---|---|---|
| Alternative 1 | [x₁₁^l, x₁₁^u] | [x₁₂^l, x₁₂^u] | … | [x₁ₙ^l, x₁ₙ^u] |
| Alternative 2 | [x₂₁^l, x₂₁^u] | [x₂₂^l, x₂₂^u] | … | [x₂ₙ^l, x₂ₙ^u] |
| ⋮ | ⋮ | ⋮ | ⋱ | ⋮ |
| Alternative m | [xₘ₁^l, xₘ₁^u] | [xₘ₂^l, xₘ₂^u] | … | [xₘₙ^l, xₘₙ^u] |

W = [w₁, w₂, …, wₙ] where wⱼ is the weight of criterion Cⱼ.

### 3.1 The Proposed Algorithm Method

The current TOPSIS method for solving MCDM problems with the interval data presents just a deterministic score for ranking. Contrary to this, when there is interval data and considering the fact that the value of each alternative with respect to each criterion can change within a range and have different behaviors, then it is logically better that ideals change in different situations as well. In other words, the definition of an ideal depends on the situation of alternatives, so to check a possibility, we propose to define ideals only for this possibility and repeat this procedure for all other alternatives.

First we calculate the normalized decision matrix as follows. The normalized values n_ij^l and n_ij^u are calculated as:

$$n_{ij}^l = \frac{x_{ij}^l}{\sqrt{\sum_{i=1}^{m} [(x_{ij}^l)^2 + (x_{ij}^u)^2]}} \quad \text{and} \quad n_{ij}^u = \frac{x_{ij}^u}{\sqrt{\sum_{i=1}^{m} [(x_{ij}^l)^2 + (x_{ij}^u)^2]}}$$

for *i* = 1, …, *m* and *j* = 1, …, *n*.

Then the interval [n_ij^l, n_ij^u] is the normalized form of interval [x_ij^l, x_ij^u]. If the criteria have different importance, we can construct the weighted normalized decision matrix as:

$$v_{ij}^l = w_i n_{ij}^l \quad \text{and} \quad v_{ij}^u = w_i n_{ij}^u$$

for *i* = 1, …, *m* and *j* = 1, …, *n*, where wᵢ is the weight of *i*th criteria and Σwᵢ = 1.

Now suppose Alternative *k*. To define the ideals, follow these steps:

**Step 1.** First set A_k (Alternative *k*) in its best situation (the lower bounds of all cost indexes and upper bounds for all benefit indexes) and set other alternatives in their best situation too. Then define A_k^{+u} in this form:

$$A_k^{+u} = \{(v_1^{+u}, v_2^{+u}, \ldots, v_n^{+u})\} = \{(\max v_{ij}^u \mid i \in O),\ (\min v_{ij}^l \mid i \in I)\}$$

where *O* is associated with benefit criteria and *I* with cost criteria.

**Step 2.** Set A_k in the worst case (upper bounds for inputs and lower bounds for outputs) and set other alternatives in their best situation. So we have:

$$A_k^{+l} = \{(v_1^{+l}, v_2^{+l}, \ldots, v_n^{+l})\} = \{(\max_{j \neq k}\{v_{ij}^u, v_{ik}^l\} \mid i \in O),\ (\min_{j \neq k}\{v_{ij}^l, v_{ik}^u\} \mid i \in I)\}$$

By this approach we can make an interval ideal to evaluate A_k. So the ideal and the negative-ideal are changed for each alternative. This is logically true because of the property that all rates are nondeterministic.

In evaluating A_k with one input (cost index) and one output (benefit index) without stating other alternatives, suppose that it is in the worst situation (point B) and other alternatives like Aⱼ are in their best situation (point D), then A_k^{+l} is made; otherwise if A_k is fixed in its best situation (point E) and Aⱼ is fixed in its best situation too (point D), then A_k^{+u} will be made. By our definition A_k^{+l} differs from A_k^{+u} and then we can determine distances as an interval number.

**Step 3.** Define A_k^{−u} in this form:

$$A_k^{-u} = \{(v_1^{-u}, v_2^{-u}, \ldots, v_n^{-u})\} = \{(\min_{j \neq k}\{v_{ij}^l, v_{ik}^u\} \mid i \in O),\ (\max_{j \neq k}\{v_{ij}^u, v_{ik}^l\} \mid i \in I)\}$$

**Step 4.** Define A_k^{−l} in this form:

$$A_k^{-l} = \{(v_1^{-l}, v_2^{-l}, \ldots, v_n^{-l})\} = \{(\min v_{ij}^l \mid i \in O),\ (\max v_{ij}^u \mid i \in I)\}$$

After determining the ideals, we need a norm to measure the distances between the alternative and ideals. Since each alternative may be an interval number and each ideal may be an interval number too, the measure must be calculated as an interval number. Using the Euclidean norm we define the following:

**Definition of distances:** We define d_k^{+u} as the distance between the worst case of A_k and A_k^{+u}:

$$d_k^{+u} = \left[\sum_{i \in I} (v_i^{+u} - v_{ik}^u)^2 + \sum_{i \in O} (v_i^{+u} - v_{ik}^l)^2\right]^{1/2} \tag{1}$$

and define d_k^{+l} in the form of:

$$d_k^{+l} = \left[\sum_{i \in I} (v_i^{+l} - v_{ik}^l)^2 + \sum_{i \in O} (v_i^{+l} - v_{ik}^u)^2\right]^{1/2} \tag{2}$$

**Theorem 1.** For the two foregoing distance definitions, the inequality d_k^{+l} ≤ d_k^{+u} holds.

*Proof.* It is sufficient to prove that for each i ∈ I: |v_i^{+l} − v_{ik}^l| ≤ |v_i^{+u} − v_{ik}^u| and for each i ∈ O: |v_i^{+l} − v_{ik}^u| ≤ |v_i^{+u} − v_{ik}^l|. Let i ∈ I. We must show |min_{j≠k}{v_{ij}^l, v_{ik}^u} − v_{ik}^l| ≤ |min{v_{ij}^l, v_{ik}^l} − v_{ik}^u|. There may be two situations: (1) Index of the minimum element in the left-hand side occurs in v_{is}^l, s ≠ k, then in the right-hand side either index of the minimum element occurs in v_{is}^l, giving |v_{is}^l − v_{ik}^l| ≤ |v_{is}^l − v_{ik}^u|, or it is in v_{ik}^l, in which the inequality still holds. (2) Index of the minimum element in the left-hand side occurs in v_{ik}^u, so the index of the minimum element in the right-hand side must be in v_{ik}^l because v_{ik}^l ≤ v_{ik}^u, then we have |v_{ik}^u − v_{ik}^l| ≤ |v_{ik}^l − v_{ik}^u|. The inequality for i ∈ O can be proved in the same manner. ∎

**Definition of d_k^{−u} and d_k^{−l}:**

- d_k^{−u}: The distance between the best situation of A_k and A_k^{−l}.
- d_k^{−l}: The distance between the worst situation of A_k and A_k^{−u}.

**Theorem 2.** The inequality d_k^{−l} ≤ d_k^{−u} holds.

*Proof.* The proof is the same as the proof of the previous theorem. ∎

After these definitions we can let R_k in this interval:

$$\frac{d_k^{-l}}{d_k^{-u} + d_k^{+u}} \leq R_k \leq \frac{d_k^{-u}}{d_k^{-l} + d_k^{+l}} \tag{3}$$

Because of the two previous theorems we have d_k^{−l}/(d_k^{−u} + d_k^{+u}) ≤ d_k^{−u}/(d_k^{−l} + d_k^{+l}), so R_k is well defined.

**Transforming to deterministic data.** If all of the alternatives have deterministic data then we have A_k^{+u} = A_k^{+l} and A_k^{−u} = A_k^{−l}, so we have d_k^{+u} = d_k^{+l} and d_k^{−u} = d_k^{−l}, then:

$$R_k = \frac{d_k^-}{d_k^- + d_k^+}$$

(the basic TOPSIS distance). If A_k^{+u} = A_k^{+l} and A_k^{−u} = A_k^{−l} but we have interval data, then d_k^{+u} ≠ d_k^{+l} and d_k^{−u} ≠ d_k^{−l}, so R_k is obtained in the interval form.

**Definition 2.** Suppose (X, Y) and (X′, Y′) are two distinct vectors. X and X′ indicate the cost indexes, Y and Y′ indicate the benefit indexes. (X, Y) dominates (X′, Y′) if (X, −Y) ≤ (X′, −Y′).

**Theorem 3.** If A_k^{+u} ≠ A_k^{+l}, then A_k^{+u} dominates A_k^{+l}.

*Proof.*
For i ∈ O: v_i^{+u} = max_{j≠k}{v_{ij}^u, v_{ik}^u} ≥ max_{j≠k}{v_{ij}^u, v_{ik}^l} = v_i^{+l}

and for i ∈ I: v_i^{+u} = min_{j≠k}{v_{ij}^l, v_{ik}^l} ≤ min_{j≠k}{v_{ij}^l, v_{ik}^u} = v_i^{+l}. ∎

### 3.2 Comparing Interval Numbers

After determining the scores in interval form, we must rank them to find the best alternative. We use two approaches for this aim:

**1. Sengupta's approach [13]:** Interval E is alternatively represented as E = ⟨m(E), w(E)⟩, where m(E) and w(E) are the mid-point and half-width of interval E:

$$m(E) = \frac{1}{2}(e^l + e^u), \quad w(E) = \frac{1}{2}(e^u - e^l)$$

After this representation, Sengupta and Pal introduced the acceptability function to compare two interval numbers E and D as follows:

$$A(<) = \frac{m(D) - m(E)}{w(D) + w(E)}$$

A(<) may be interpreted as the "first interval to be inferior to the second interval". Here the term 'inferior to' ('superior to') is analogous to 'less than' ('greater than'). The decision maker can decide to select one of the two interval numbers (for maximizing or minimizing) by the value of A(<). This procedure states that between two interval numbers with the same mid-point, the less uncertain interval will be the best choice for both of maximization and minimization.

**2. Delgado's approach [14]:** Delgado et al. presented two notions: Value and Ambiguous for a fuzzy number. It is possible to represent an interval by a trapezoidal fuzzy number. In this case the value (V) and the ambiguous (A) of an interval number is the same as the midpoint and half-width of it, like that of Sengupta's approach. They suggested the following steps to rank two interval numbers E and D:

- **Step 1:** Compare V(E) and V(D). If they are "approximately equal" then go to the next step. Otherwise rank E and D according to the relative position of V(E) and V(D).
- **Step 2:** Compare A(E) with A(D). If they are "approximately equal" then conclude that E and D are indifferent (almost equal). Otherwise rank them by considering decision maker's attitude toward the uncertainty and the relative position of A(E) and A(D). In other words, a decision maker with an optimistic attitude toward the uncertainty could prefer the interval with greater width, whereas a pessimistic decision maker could prefer the interval with small width.

In the following example we use both of these methods.

---

## 4. Empirical Example

In this section we work on our algorithm for six cities in Iran to find the best place for creating a date factory. These cities must be evaluated by four criteria, two of them are cost oriented and the others are benefit oriented (shown as input and output factors respectively). Criteria are as follows:

- **Input 1:** Distance from border (km)
- **Input 2:** Cost of creating the factory (1000$)
- **Output 1:** Finance (percent)
- **Output 2:** Product in the region (Ton)

The first criterion is a real number and the others are in interval form. Table 3 represents the data.

**Table 3: The data of alternatives**

| City | Input 1 | Input 2 | Output 1 | Output 2 |
|---|---|---|---|---|
| City 1 | 1451 | [2551, 3118] | [40, 50] | [153, 187] |
| City 2 | 843 | [3742, 4573] | [63, 77] | [459, 561] |
| City 3 | 1125 | [3312, 4049] | [48, 58] | [153, 187] |
| City 4 | 55 | [5309, 6488] | [72, 88] | [347, 426] |
| City 5 | 356 | [3709, 4534] | [59, 71] | [151, 189] |
| City 6 | 391 | [4884, 5969] | [72, 88] | [388, 474] |

**Table 4: The normalized rates**

| City | Input 1 | Input 2 | Output 1 | Output 2 |
|---|---|---|---|---|
| City 1 | 0.694 | [0.163, 0.200] | [0.172, 0.215] | [0.130, 0.159] |
| City 2 | 0.403 | [0.240, 0.293] | [0.270, 0.331] | [0.391, 0.477] |
| City 3 | 0.538 | [0.212, 0.259] | [0.206, 0.250] | [0.130, 0.159] |
| City 4 | 0.026 | [0.340, 0.416] | [0.309, 0.378] | [0.295, 0.362] |
| City 5 | 0.170 | [0.238, 0.291] | [0.253, 0.305] | [0.128, 0.161] |
| City 6 | 0.187 | [0.313, 0.383] | [0.309, 0.378] | [0.330, 0.403] |

Since all the criteria have the same importance, it is not necessary to use the weights. In other words, all the weights of criteria are equal. Using the proposed approach, we can construct ideal and negative-ideal for each alternative. Then we deal with formulae (1)–(3) to determine the efficiency interval for each city.

**Table 5: Ideals and distances for City 2**

| | Input 1 | Input 2 | Output 1 | Output 2 | d^{+u} | d^{+l} | d^{−u} | d^{−l} |
|---|---|---|---|---|---|---|---|---|
| A₂^{+u} | 0.026 | 0.163 | 0.378 | 0.477 | | | | |
| A₂^{+l} | 0.026 | 0.163 | 0.378 | 0.403 | | | | |
| A₂^{−u} | 0.694 | 0.416 | 0.172 | 0.128 | | | | |
| A₂^{−l} | 0.694 | 0.416 | 0.172 | 0.128 | 0.422 | 0.394 | 0.512 | 0.423 |

Interval score for City 2 by formula (3) is: **[0.452, 0.623]**.

Both Sengupta's approach and Delgado's approach give the same ranking result, shown in Table 6.

**Table 6: Comparing alternatives**

| Alternative | Interval Efficiency | Mid-point V(E) | Half-width A(E) | Ranking |
|---|---|---|---|---|
| City 1 | [0.210, 0.263] | 0.237 | 0.026 | 6 |
| City 2 | [0.452, 0.623] | 0.540 | 0.088 | 4 |
| City 3 | [0.243, 0.320] | 0.282 | 0.039 | 5 |
| City 4 | [0.663, 0.810] | 0.737 | 0.074 | 1 |
| City 5 | [0.551, 0.628] | 0.590 | 0.038 | 3 |
| City 6 | [0.601, 0.780] | 0.690 | 0.090 | 2 |

Now suppose the decision maker believes that City 4 and City 6 have approximately equal mid-point. If he/she has an optimistic attitude towards the uncertainty, he/she would prefer City 6 as the best; otherwise City 4 will be selected as the best option.

---

## 5. Conclusion

In this paper we have presented a new TOPSIS method with interval data. This new method can sort units by interval efficiency due to the nature of data. If the data are real numbers, this new method is the same as the current TOPSIS method. We dealt with this method to find the best position for creating a factory. At the end, the two methods for ranking interval numbers were applied to find the best alternative.

---

## Acknowledgments

The authors wish to sincerely thank the two anonymous referees for their valuable comments. Also the authors wish to express their gratitude to Dr. V. Dehnavi for his helpful suggestions and for editing this paper.

---

## References

[1] C.L. Hwang, K. Yoon, *Multiple Attribute Decision Making Methods and Applications*, Springer, Berlin, Heidelberg, 1981.

[2] Zeleny, *Multiple Criteria Decision Making*, McGraw-Hill, New York, 1982.

[3] Y.J. Lai, T.Y. Liu, C.L. Hwang, TOPSIS for MODM, *European Journal of Operational Research* 76 (3) (1994) 486–500.

[4] M.A. Abo-Sinna, A.H. Amer, Extensions of TOPSIS for multi-objective large-scale nonlinear programming problems, *Applied Mathematics and Computation* 162 (1) (2005) 243–256.

[5] D.L. Olson, Comparison of weights in TOPSIS models, *Mathematical and Computer Modelling* 40 (7–8) (2004) 721–727.

[6] M.S. Kuo, G.H. Tzeng, W.C. Huang, Group decision-making based on concepts of ideal and anti-ideal points in a fuzzy environment, *Mathematical and Computer Modelling* 45 (3–4) (2007) 324–339.

[7] H.S. Shih, H.J. Shyur, E.S. Lee, An extension of TOPSIS for group decision making, *Mathematical and Computer Modelling* 45 (7–8) (2007) 801–813.

[8] G.R. Jahanshahloo, F. Hosseinzadeh Lotfi, M. Izadikhah, An algorithmic method to extend TOPSIS for decision-making problems with interval data, *Applied Mathematics and Computation* 175 (2) (2006) 1375–1384.

[9] S.J. Chen, C.L. Hwang, *Fuzzy Multiple Attribute Decision Making: Methods and Applications*, Springer-Verlag, Berlin, 1992.

[10] T.Y. Chen, C.Y. Tsao, The interval-valued fuzzy TOPSIS method and experimental analysis, *Fuzzy Sets and Systems* 159 (11) (2008) 1410–1428.

[11] J. Zhang, D. Wu, D.L. Olson, The method of grey related analysis to multiple attribute decision making problems with interval numbers, *Mathematical and Computer Modelling* 42 (9–10) (2005) 991–998.

[12] Y.M. Wang, T.M.S. Elhag, Fuzzy TOPSIS method based on alpha level sets with an application to bridge risk assessment, *Expert Systems with Applications* 31 (2) (2006) 309–319.

[13] A. Sengupta, T.K. Pal, On comparing interval numbers, *European Journal of Operational Research* 127 (1) (2000) 28–43.

[14] M. Delgado, M.A. Vila, W. Voxman, On a canonical representation of fuzzy numbers, *Fuzzy Sets and Systems* 94 (1) (1998) 205–216.

[15] M. Delgado, M.A. Vila, W. Voxman, A fuzziness measure for fuzzy numbers: Applications, *Fuzzy Sets and Systems* 93 (2) (1998) 125–135.

[16] S. Chanas, P. Zielinski, Ranking fuzzy interval numbers in the setting of random sets — further results, *Information Sciences* 117 (3–4) (1999) 191–200.

[17] R.E. Moore, *Method and Application of Interval Analysis*, SIAM, Philadelphia, 1979.

[18] A. Sengupta, T.K. Pal, A-index for ordering interval numbers, Presented in Indian Science Congress 1997, Delhi University, 1997.
